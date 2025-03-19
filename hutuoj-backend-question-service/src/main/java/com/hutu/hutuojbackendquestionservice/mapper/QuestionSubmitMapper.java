package com.hutu.hutuojbackendquestionservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hutu.hutuojmodel.model.dto.question.UserSubmitStatsDTO;
import com.hutu.hutuojmodel.model.entity.QuestionSubmit;

import java.util.List;

/**
* @author 478234818
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2023-08-09 15:41:52
* @Entity generator.domain.QuestionSubmit
*/
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {

    List<UserSubmitStatsDTO> selectTopPassedUsers();

}




