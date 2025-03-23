package com.hutu.hutuojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hutu.hutuojmodel.model.dto.question.QuestionQueryRequest;
import com.hutu.hutuojmodel.model.entity.Comment;
import com.hutu.hutuojmodel.model.entity.Question;
import com.hutu.hutuojmodel.model.vo.QuestionVO;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 478234818
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-08-09 15:40:54
*/
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);


    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 获取推荐题目推荐列表
     * @param userId
     * @return
     */
    List<QuestionVO> getRecommendations(Long userId);

    /**
     * 根据题目id获取评论区信息
     * @param questionId
     * @return
     */
    List<Comment> getCommentListByPage(Long questionId);


    Long addComment(Comment comment);

    Boolean deleteComment(Long id);
}
