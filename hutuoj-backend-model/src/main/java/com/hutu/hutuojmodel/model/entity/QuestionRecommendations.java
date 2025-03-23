package com.hutu.hutuojmodel.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目推荐实体类
 * @author hutu-g
 * @TableName question_submit
 */
@TableName(value ="question_recommendations")
@Data
public class QuestionRecommendations implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 推荐结果
     */
    private String recommendations;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}