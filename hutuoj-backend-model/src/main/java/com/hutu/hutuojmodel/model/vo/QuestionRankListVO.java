package com.hutu.hutuojmodel.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author hutu-g
 * @ createTime 2025-03-03
 * @ description 排名返回
 * @ version 1.0
 */
@Data
@Accessors(chain = true) // 允许链式调用
public class QuestionRankListVO implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名字
     */
    private String userName;

    /**
     * 通过数量
     */
    private Integer acceptedQuestionNum;

    /**
     * 用户提交数量
     */
    private Integer submitQuestionNum;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
