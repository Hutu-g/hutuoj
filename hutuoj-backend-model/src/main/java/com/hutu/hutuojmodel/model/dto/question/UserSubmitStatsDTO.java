package com.hutu.hutuojmodel.model.dto.question;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hutu-g
 * @ createTime 2025-03-04
 * @ description 排名数据库查询DTO
 * @ version 1.0
 */
@Data
@Accessors(chain = true)
public class UserSubmitStatsDTO {
    private Long userId;

    private String submitQuestionNum;

    private String acceptedQuestionNum;
}