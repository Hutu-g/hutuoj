package com.hutu.hutuojbackendquestionservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hutu.hutuojmodel.model.dto.question.UserSubmitStatsDTO;
import com.hutu.hutuojmodel.model.entity.Question;
import com.hutu.hutuojmodel.model.entity.QuestionRecommendations;

import java.util.List;

/**
* @author 478234818
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2023-08-09 15:40:54
* @Entity generator.domain.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    QuestionRecommendations selectQuestionRecommendations(Long userId);
}




