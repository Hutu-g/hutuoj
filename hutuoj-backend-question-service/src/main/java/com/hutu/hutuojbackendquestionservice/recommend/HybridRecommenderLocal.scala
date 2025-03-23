package com.hutu.hutuojbackendquestionservice.recommend

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StringIndexer, StringIndexerModel}
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

object HybridRecommenderLocal {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("HybridRecommenderLocal")
      .master("local[*]")
      .config("spark.driver.memory", "4g")
      .config("spark.sql.shuffle.partitions", "10")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // 1. 加载数据（严格校验judgeInfo字段）
    val (questionsDF, submitDF) = loadData(spark)

    // 2. 生成推荐
    val finalRecs = generateRecommendations(spark, questionsDF, submitDF)

    // 3. 写入MySQL
    writeToMySQL(finalRecs)

    spark.stop()
  }

  def loadData(spark: SparkSession): (DataFrame, DataFrame) = {
    // MySQL连接配置
    val jdbcOpts = Map(
      "url" -> "jdbc:mysql://localhost:3306/hutu_oj?useSSL=false",
      "user" -> "root",
      "password" -> "123456",
      "driver" -> "com.mysql.cj.jdbc.Driver"
    )

    // 加载question表（验证tags为JSON数组）
    val questionsDF = spark.read
      .format("jdbc")
      .options(jdbcOpts)
      .option("dbtable", "question")
      .load()
      .select(
        col("id").cast(StringType).as("questionId"),
        col("tags").as("tagsJson")  // 示例：["中等","字符串"]
      )

    // 加载question_submit表（严格解析judgeInfo）
    val submitDF = spark.read
      .format("jdbc")
      .options(jdbcOpts)
      .option("dbtable", "question_submit")
      .load()
      .filter(col("isDelete") === 0)  // 过滤已删除
      .select(
        col("userId").cast(StringType),
        col("questionId").cast(StringType),
        col("status").cast(IntegerType),
        col("judgeInfo").as("judgeInfoJson")  // 示例：{"message":"成功", "memory":2875392}
      )
      // 严格解析message字段
      .withColumn("message", json_tuple(col("judgeInfoJson"), "message"))
      .filter(col("message").isNotNull)  // 过滤无message的记录
      .withColumn("isSuccess", col("message") === "成功")

    (questionsDF, submitDF.cache())
  }

  def generateRecommendations(spark: SparkSession, questionsDF: DataFrame, submitDF: DataFrame): DataFrame = {
    // 协同过滤部分（仅使用成功提交）
    val alsRecs = collaborativeFiltering(spark, submitDF)

    // 基于内容的推荐
    val contentRecs = contentBasedRecommendation(spark, questionsDF, submitDF)

    // 混合推荐
    hybridRecommendation(alsRecs, contentRecs)
  }

  def collaborativeFiltering(spark: SparkSession, submitDF: DataFrame): DataFrame = {
    // 添加隐式转换导入
    import spark.implicits._

    // 1. 使用StringIndexer转换ID类型
    val userIndexer = new StringIndexer()
      .setInputCol("userId")
      .setOutputCol("userIdInt")
      .setHandleInvalid("skip")

    val questionIndexer = new StringIndexer()
      .setInputCol("questionId")
      .setOutputCol("questionIdInt")
      .setHandleInvalid("skip")

    val pipeline = new Pipeline()
      .setStages(Array(userIndexer, questionIndexer))

    val model = pipeline.fit(submitDF)
    val indexedDF = model.transform(submitDF)
      .withColumn("userIdInt", $"userIdInt".cast(IntegerType))
      .withColumn("questionIdInt", $"questionIdInt".cast(IntegerType))

    // 2. 构建评分矩阵
    val ratings = indexedDF
      .filter(col("isSuccess") && col("status") === 2)
      .groupBy("userIdInt", "questionIdInt")
      .agg(lit(1).as("rating")) // 改为二元评分

    // 3. 训练ALS模型
    val als = new ALS()
      .setMaxIter(15) // 增加迭代次数
      .setRank(50)    // 增加隐向量维度
      .setRegParam(0.01) // 降低正则化强度
      .setImplicitPrefs(true)
      .setUserCol("userIdInt")
      .setItemCol("questionIdInt")
      .setRatingCol("rating")
      .setColdStartStrategy("drop")

    val alsModel = als.fit(ratings)

    // 4. 生成推荐并转换回原始ID
    val recommendations = alsModel.recommendForAllUsers(10)
      .select($"userIdInt", explode($"recommendations").as("recommendation"))
      .select($"userIdInt", $"recommendation.questionIdInt", $"recommendation.rating".as("alsScore"))

    // 获取索引与原始ID的映射
    val userLabels = model.stages(0).asInstanceOf[StringIndexerModel].labels
    val questionLabels = model.stages(1).asInstanceOf[StringIndexerModel].labels

    // 修正点：使用spark.createDataFrame代替toDF
    val userMap = spark.createDataFrame(
      userLabels.zipWithIndex.map { case (id, index) => (index.toInt, id) }
    ).toDF("userIdInt", "userId")

    val questionMap = spark.createDataFrame(
      questionLabels.zipWithIndex.map { case (id, index) => (index.toInt, id) }
    ).toDF("questionIdInt", "questionId")

    // 5. 连接映射关系
    val alsResults = recommendations
      .join(userMap, "userIdInt")
      .join(questionMap, "questionIdInt")
      .select("userId", "questionId", "alsScore")

    // 添加最终ALS结果日志
    println("\n=== 最终ALS推荐前20条 ===")
    alsResults.show(20, truncate = false)

    alsResults
  }

  def contentBasedRecommendation(spark: SparkSession, questionsDF: DataFrame, submitDF: DataFrame): DataFrame = {

    // 1. 解析题目标签
    val questionTags = questionsDF
      .withColumn("tags", from_json(col("tagsJson"), ArrayType(StringType)))
      .na.drop(Seq("tags"))
      .select(col("questionId").cast(LongType), explode(col("tags")).as("tag"))

    // 2. 分离正确和错误提交
    val correctSubmits = submitDF
      .filter(col("isSuccess") && col("status") === 2)
      .select(col("userId").cast(LongType), col("questionId").cast(LongType))
      .distinct()

    val errorSubmits = submitDF
      .filter(!col("isSuccess") || col("status") =!= 2)
      .select(col("userId").cast(LongType), col("questionId").cast(LongType))

    // 3. 构建用户-标签权重（统一列名）
    val userTagWeights = errorSubmits
      .join(questionTags, "questionId")
      .groupBy("userId", "tag")
      .agg(count("tag").as("count")) // 统一列名为count
      .withColumn("tagScore", log1p(col("count")) * 0.7)
      .select("userId", "tag", "tagScore")

    // 4. 合并正确提交的负反馈
    val correctTagWeights = correctSubmits
      .join(questionTags, "questionId")
      .groupBy("userId", "tag")
      .agg(count("tag").as("count")) // 统一列名为count
      .withColumn("tagScore", -log1p(col("count")) * 0.3)
      .select("userId", "tag", "tagScore")

    val combinedWeights = userTagWeights.unionByName(correctTagWeights)
      .groupBy("userId", "tag")
      .agg(sum("tagScore").as("finalScore"))

    // 5. 生成推荐候选
    val recommendations = combinedWeights
      .join(questionTags, "tag")
      .groupBy("userId", "questionId")
      .agg(sum("finalScore").as("matchScore"))
      .orderBy(desc("matchScore"))

    // 6. 过滤已正确题目
    val filteredRecs = recommendations.join(
      correctSubmits,
      Seq("userId", "questionId"),
      "left_anti"
    )

    // 7. Top10推荐（修改返回结构）
    val windowSpec = Window.partitionBy("userId").orderBy(desc("matchScore"))
    val finalRecs = filteredRecs
      .withColumn("rank", rank().over(windowSpec))
      .filter(col("rank") <= 10)
      .select("userId", "questionId", "matchScore")  // 直接返回结构化数据

    // 添加调试日志
    println("\n=== 最终内容推荐样例 ===")
    finalRecs.show(5, false)

    // 返回需要的字段
    finalRecs.select(
      col("userId").cast(StringType),
      col("questionId").cast(StringType),
      col("matchScore").as("contentScore")
    )
  }

  def hybridRecommendation(alsRecs: DataFrame, contentRecs: DataFrame): DataFrame = {
    // 对齐schema
    val als = alsRecs.select(
      col("userId"),
      col("questionId"),
      col("alsScore").as("score")
    )

    val content = contentRecs.select(
      col("userId"),
      col("questionId"),
      col("contentScore").as("score")
    )

    // 混合策略（保留原始结构）
    val combined = als.union(content)
      .groupBy("userId", "questionId")
      .agg(max("score").as("finalScore"))  // 使用max保留最高分

    // 最终推荐（延后JSON转换）
    combined.groupBy("userId")
      .agg(collect_list(struct(col("questionId"), col("finalScore"))).as("recommendations"))
      .withColumn("recommendations",
        expr("slice(sort_array(recommendations, false), 1, 10)"))
      .withColumn("recommendations", to_json(col("recommendations")))
  }

  def writeToMySQL(df: DataFrame): Unit = {

    // 添加类型转换
    val outputDF = df
      .withColumn("userId", col("userId").cast(LongType))
      .withColumn("recommendations", col("recommendations").cast(StringType))
    outputDF.write
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/hutu_oj")
      .option("dbtable", "question_recommendations")
      .option("user", "root")
      .option("password", "123456")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .mode("overwrite")
      .save()
  }
}