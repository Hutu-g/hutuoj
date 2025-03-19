package com.hutu.hutuojmodel.model.dto.questionSubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hutu-g
 * @ createTime 2025-03-01
 * @ description 用户当前题目的提交记录
 * @ version 1.0
 */
@Data
public class QuestionSubmitListRequest implements Serializable {
    /**
     * 用户 id
     */
    private Long userId;


    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 当前页码，默认为1
     */
    private Integer currentPage = 1;

    /**
     * 页面尺寸，默认为 10
     */
    private Integer pageSize = 10;

    private static final long serialVersionUID = 1L;
}
