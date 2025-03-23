package com.hutu.hutuojbackendquestionservice.recommend;
import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;
import org.apache.spark.sql.types.DataTypes;

import static org.apache.spark.sql.functions.*;
/**
 * @author hutu-g
 * @ createTime 2025-03-22
 * @ description 基于用户答题的题目标签进行内容推荐
 * @ version 1.0
 */


public class TagBasedRecommender {

    public static void main(String[] args) {


        // JDBC配置参数
        String jdbcUrl = "jdbc:mysql://localhost:3306/hutu_oj";
        String jdbcUser = "root";
        String jdbcPassword = "123456";
        SparkSession spark = SparkSession.builder()
                .appName("TagBasedRecommender")
                .master("local[*]")
                .config("spark.sql.shuffle.partitions", "4")
                .config("spark.driver.host", "localhost")
                .getOrCreate();

        try {
            // ==================== 1. 加载数据 ====================
            // 错误提交数据（judgeInfo.message != 成功）
            Dataset<Row> errorSubmits = spark.read()
                    .format("jdbc")
                    .option("url", jdbcUrl)
                    .option("dbtable", "question_submit")
                    .option("user", jdbcUser)
                    .option("password", jdbcPassword)
                    .load()
                    .filter("isDelete = 0")
                    .withColumn("judgeMessage", json_tuple(col("judgeInfo"), "message"))
                    .filter("judgeMessage != '成功'")
                    .select(col("userId").cast("long"), col("questionId").cast("long"));

            // 正确提交数据（judgeInfo.message = 成功）
            Dataset<Row> correctSubmits = spark.read()
                    .format("jdbc")
                    .option("url", jdbcUrl)
                    .option("dbtable", "question_submit")
                    .option("user", jdbcUser)
                    .option("password", jdbcPassword)
                    .load()
                    .filter("isDelete = 0")
                    .withColumn("judgeMessage", json_tuple(col("judgeInfo"), "message"))
                    .filter("judgeMessage = '成功'")
                    .select(col("userId").cast("long"), col("questionId").cast("long"))
                    .distinct();

            // 题目标签数据
            Dataset<Row> questionDF = spark.read()
                    .format("jdbc")
                    .option("url", jdbcUrl)
                    .option("dbtable", "question")
                    .option("user", jdbcUser)
                    .option("password", jdbcPassword)
                    .load()
                    .select(col("id").cast("long").alias("questionId"),
                            from_json(col("tags"), DataTypes.createArrayType(DataTypes.StringType)).alias("tags"));

            System.out.println("===== 错误提交样例 =====");
            errorSubmits.show(5);
            System.out.println("错误提交总数: " + errorSubmits.count());

            System.out.println("\n===== 题目标签样例 =====");
            questionDF.show(5);
            System.out.println("题目总数: " + questionDF.count());

            // ==================== 2. 构建用户-标签偏好 ====================
            Dataset<Row> userTagWeights = errorSubmits
                    .join(questionDF, "questionId")
                    .select("userId", "questionId", "tags")
                    .withColumn("tag", explode(col("tags")))
                    .groupBy("userId", "tag")
                    .agg(count("tag").alias("tagCount"))
                    .withColumn("tagScore", log1p(col("tagCount")));

            System.out.println("\n===== 用户标签权重 =====");
            userTagWeights.show(10);

            // 在构建用户-标签偏好后添加
            System.out.println("\n===== 有效用户标签分布 =====");
            userTagWeights.groupBy("userId").count().orderBy(desc("count")).show();

            // ==================== 3. 生成推荐候选 ====================
            Dataset<Row> questionTags = questionDF
                    .select("questionId", "tags")
                    .withColumn("tag", explode(col("tags")));

            Dataset<Row> recommendations = userTagWeights
                    .join(questionTags, "tag")
                    .groupBy("userId", "questionId")
                    .agg(sum("tagScore").alias("matchScore"))
                    .orderBy(desc("matchScore"));

            System.out.println("\n===== 推荐候选列表 =====");
            recommendations.show(10);
            // 在生成推荐候选后添加
            System.out.println("\n===== 推荐候选用户覆盖 =====");
            recommendations.select("userId").distinct().show();

            // ==================== 4. 过滤已正确题目 ====================
            Dataset<Row> filteredRecs = recommendations.join(
                    correctSubmits,
                    recommendations.col("userId").equalTo(correctSubmits.col("userId"))
                            .and(recommendations.col("questionId").equalTo(correctSubmits.col("questionId"))),
                    "left_anti"
            );
            // 在过滤正确题目后添加
            System.out.println("\n===== 过滤后用户列表 =====");
            filteredRecs.select("userId").distinct().show();

            // ==================== 5. Top10推荐 ====================
            WindowSpec window = Window.partitionBy("userId").orderBy(desc("matchScore"));
            Dataset<Row> finalOutput = filteredRecs
                    .withColumn("rank", rank().over(window))
                    .filter(col("rank").leq(10))
                    .drop("rank")
                    .groupBy("userId")
                    .agg(collect_list("questionId").alias("recommendedQuestions"))
                    .withColumn("recommendedQuestions", to_json(col("recommendedQuestions")));

            System.out.println("\n===== 最终推荐结果 =====");
            finalOutput.show(5, false);

            // ==================== 6. 保存结果 ====================
            finalOutput.write()
                    .format("jdbc")
                    .option("url", jdbcUrl)
                    .option("dbtable", "tag_recommendations")
                    .option("user", jdbcUser)
                    .option("password",jdbcPassword)
                    .mode(SaveMode.Overwrite)
                    .save();

        } finally {
            spark.stop();
        }
    }
}