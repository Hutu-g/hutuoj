package com.hutu.hutuojbackendquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;

import com.hutu.hutuojbackendquestionservice.service.QuestionService;
import com.hutu.hutuojbackendquestionservice.service.QuestionSubmitService;
import com.hutu.hutuojbackendserviceclient.service.UserFeignClient;
import com.hutu.hutuojcommon.annotation.AuthCheck;
import com.hutu.hutuojcommon.common.BaseResponse;
import com.hutu.hutuojcommon.common.DeleteRequest;
import com.hutu.hutuojcommon.common.ErrorCode;
import com.hutu.hutuojcommon.common.ResultUtils;
import com.hutu.hutuojcommon.constant.UserConstant;
import com.hutu.hutuojcommon.exception.BusinessException;
import com.hutu.hutuojcommon.exception.ThrowUtils;
import com.hutu.hutuojmodel.model.dto.Comment.CommentAddRequest;
import com.hutu.hutuojmodel.model.dto.question.*;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionRankListRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitListRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.hutu.hutuojmodel.model.entity.Comment;
import com.hutu.hutuojmodel.model.entity.Question;
import com.hutu.hutuojmodel.model.entity.User;
import com.hutu.hutuojmodel.model.vo.CurrentQuestionSubmitVO;
import com.hutu.hutuojmodel.model.vo.QuestionRankListVO;
import com.hutu.hutuojmodel.model.vo.QuestionSubmitVO;
import com.hutu.hutuojmodel.model.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private QuestionSubmitService questionSubmitService;


    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }

        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }

        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //不是本人不能获取全部信息
        User loginUser = userFeignClient.getLoginUser(request);
        if (!question.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据 id 获取(脱敏)
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }

        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（自己的所有提交记录）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        if (userFeignClient.getLoginUser(request).getId() == null) {
            ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        //返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitQueryRequest));
    }

    /**
     * 通过用户 id 和 题目 id 获取题目提交列表（分页）
     */
    @PostMapping("/question_submit/currentQuestion/list/page")
    public BaseResponse<Page<CurrentQuestionSubmitVO>> currentQuestionSubmitByPage(@RequestBody QuestionSubmitListRequest questionSubmitListRequest) {
        //返回脱敏信息
        return ResultUtils.success(questionSubmitService.listQuestionSubmitVOByUserIdByQuestionId(questionSubmitListRequest));
    }

    /**
     * 获取排行榜
     * @param questionRankListRequest
     * @param request
     * @return
     */
    @PostMapping("/ranking")
    public BaseResponse<Page<QuestionRankListVO>> getUserRankingList(@RequestBody QuestionRankListRequest questionRankListRequest, HttpServletRequest request) {
        if (userFeignClient.getLoginUser(request).getId() == null) {
            ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        //返回脱敏信息
        return ResultUtils.success(questionSubmitService.getUserRankingList(questionRankListRequest));
    }


    /**
     * 获取推荐题目推荐列表
     * @param request
     * @return
     */
    @GetMapping("/get/recommendations")
    public BaseResponse<List<QuestionVO>> getRecommendations(HttpServletRequest request) {
        Long userId = userFeignClient.getLoginUser(request).getId();
        if (userId == null) {
            ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        return ResultUtils.success(questionService.getRecommendations(userId));
    }

    /**
     * 根据题目id获取评论区信息
     * @param questionId
     * @param request
     * @return
     */
    @GetMapping("/comment/get/list")
    public BaseResponse<List<Comment>> getCommentList(long questionId, HttpServletRequest request) {
        Long userId = userFeignClient.getLoginUser(request).getId();
        if (userId == null) {
            ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(questionService.getCommentListByPage(questionId));
    }

    /**
     * 新增评论
     * @param commentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/comment/add")
    public BaseResponse<Long> addComment(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        //校验登录数据
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser.getId() == null) {
            ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        //校验上传数据
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (commentAddRequest.getContent().length() >100){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数过长");
        }


        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddRequest, comment);
        comment.setUserId(loginUser.getId());
        return ResultUtils.success(questionService.addComment(comment));
    }

    /**
     * 删除
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/comment/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        //校验登录数据
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser.getId() == null) {
            ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        //校验参数数据
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(questionService.deleteComment(deleteRequest.getId()));
    }











}
