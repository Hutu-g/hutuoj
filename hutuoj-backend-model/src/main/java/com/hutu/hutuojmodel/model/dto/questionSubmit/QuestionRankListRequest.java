package com.hutu.hutuojmodel.model.dto.questionSubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hutu-g
 * @ createTime 2025-03-03
 * @ description 排行请求
 * @ version 1.0
 */
@Data
public class QuestionRankListRequest implements Serializable {
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
