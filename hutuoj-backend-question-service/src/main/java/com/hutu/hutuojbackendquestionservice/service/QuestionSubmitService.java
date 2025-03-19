package com.hutu.hutuojbackendquestionservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionRankListRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitListRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.hutu.hutuojmodel.model.entity.QuestionSubmit;
import com.hutu.hutuojmodel.model.entity.User;
import com.hutu.hutuojmodel.model.vo.CurrentQuestionSubmitVO;
import com.hutu.hutuojmodel.model.vo.QuestionRankListVO;
import com.hutu.hutuojmodel.model.vo.QuestionSubmitVO;


/**
* @author 478234818
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-08-09 15:41:52
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long  doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);



    /**
     * 通过用户 id 和 题目 id 获取题目提交列表（分页）
     * @param questionSubmitListRequest
     * @return
     */
    Page<CurrentQuestionSubmitVO> listQuestionSubmitVOByUserIdByQuestionId(QuestionSubmitListRequest questionSubmitListRequest);

    /**
     * 分页获取题目提交列表（自己的所有提交记录）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取排行榜
     * @param questionRankListRequest
     * @return
     */
    Page<QuestionRankListVO> getUserRankingList(QuestionRankListRequest questionRankListRequest);
}
