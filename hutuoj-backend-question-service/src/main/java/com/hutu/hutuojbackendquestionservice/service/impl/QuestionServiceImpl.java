package com.hutu.hutuojbackendquestionservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hutu.hutuojbackendquestionservice.mapper.QuestionMapper;
import com.hutu.hutuojbackendquestionservice.service.CommentService;
import com.hutu.hutuojbackendquestionservice.service.QuestionService;
import com.hutu.hutuojbackendserviceclient.service.UserFeignClient;
import com.hutu.hutuojcommon.common.ErrorCode;
import com.hutu.hutuojcommon.constant.CommonConstant;
import com.hutu.hutuojcommon.exception.BusinessException;
import com.hutu.hutuojcommon.exception.ThrowUtils;
import com.hutu.hutuojcommon.utils.SqlUtils;
import com.hutu.hutuojmodel.model.dto.question.QuestionQueryRequest;
import com.hutu.hutuojmodel.model.entity.Comment;
import com.hutu.hutuojmodel.model.entity.Question;
import com.hutu.hutuojmodel.model.entity.QuestionRecommendations;
import com.hutu.hutuojmodel.model.entity.User;
import com.hutu.hutuojmodel.model.vo.QuestionVO;
import com.hutu.hutuojmodel.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 478234818
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-08-09 15:40:54
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentService commentService;


    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传的请求对象，得到mybatis框架支持的查询类QueryWrapper）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条 件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);

        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        long questionId = question.getId();
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> questionIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> questionIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> questionIdSet = questionList.stream().map(Question::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//        }
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userFeignClient.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    /**
     * 获取推荐题目推荐列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<QuestionVO> getRecommendations(Long userId) {
        //查询推荐列表
        QuestionRecommendations questionRecommendations = baseMapper.selectQuestionRecommendations(userId);
        List<Question> questions;
        if (questionRecommendations == null || StringUtils.isBlank(questionRecommendations.getRecommendations())) {
            // 随机推荐10题（MySQL随机语法）
            log.info("随机推荐10题");
            questions = getRandomQuestions(10);
        } else {
            log.info("查询推荐表题");
            List<Long> questionIds = parseRecommendationJson(questionRecommendations.getRecommendations());
            questions = baseMapper.selectBatchIds(questionIds);
        }
        return questions.stream().map(QuestionVO::objToVo).collect(Collectors.toList());
    }

    //获取所有questionId字段
    private List<Long> parseRecommendationJson(String recommendations) {
        List<Long> questionIds = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(recommendations);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Long questionId = jsonObject.getLong("questionId");
            if (questionId != null) {
                questionIds.add(questionId);
            }
        }
        return questionIds;
    }

    //随机获取10道题目
    private List<Question> getRandomQuestions(int count) {
        QueryWrapper<Question> queryWrapper = Wrappers.query();
        // 使用 ORDER BY RAND() 进行随机排序
        queryWrapper.last("ORDER BY RAND() LIMIT 10");
        // 执行查询
        return this.list(queryWrapper);
    }

    /**
     * 根据题目id获取评论区信息
     *
     * @param questionId
     * @return
     */
    @Override
    public List<Comment> getCommentListByPage(Long questionId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getQuestionId, questionId);
        //构建评论树构建评论树并返回
        return processComments(commentService.list(wrapper));
    }

    //构建评论树
    public static List<Comment> processComments(List<Comment> list) {
        // (id, Comment)
        Map<Long, Comment> map = new HashMap<>();
        //result是一级评论列表
        List<Comment> result = new ArrayList<>();
        // 将所有根评论加入 map
        for (Comment comment : list) {
            if (comment.getParentId() == null) {
                result.add(comment);
            }
            map.put(comment.getId(), comment);
        }
        // 子评论加入到父评论的 children 中
        for (Comment comment : list) {
            Long parentId = comment.getParentId();
            // 当前评论为子评论
            if (parentId != null) {
                Comment p = map.get(parentId);
                // children 为空，则创建
                if (p.getChildren() == null) {
                    p.setChildren(new ArrayList<>());
                }
                p.getChildren().add(comment);
            }
        }
        return result;
    }

    @Override
    public Long addComment(Comment comment) {
        boolean save = commentService.save(comment);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return comment.getId();
    }

    @Override
    public Boolean deleteComment(Long id) {
        return commentService.removeById(id);
    }


}




