package com.hutu.hutuojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hutu.hutuojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.hutu.hutuojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.hutu.hutuojbackendquestionservice.service.QuestionService;
import com.hutu.hutuojbackendquestionservice.service.QuestionSubmitService;
import com.hutu.hutuojbackendserviceclient.service.JudgeFeignClient;
import com.hutu.hutuojbackendserviceclient.service.UserFeignClient;
import com.hutu.hutuojcommon.common.ErrorCode;
import com.hutu.hutuojcommon.constant.CommonConstant;
import com.hutu.hutuojcommon.exception.BusinessException;
import com.hutu.hutuojcommon.utils.SqlUtils;
import com.hutu.hutuojmodel.model.dto.question.UserSubmitStatsDTO;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionRankListRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitListRequest;
import com.hutu.hutuojmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.hutu.hutuojmodel.model.entity.Question;
import com.hutu.hutuojmodel.model.entity.QuestionSubmit;
import com.hutu.hutuojmodel.model.entity.User;
import com.hutu.hutuojmodel.model.enums.QuestionSubmitLanguageEnum;
import com.hutu.hutuojmodel.model.enums.QuestionSubmitStatusEnum;
import com.hutu.hutuojmodel.model.vo.CurrentQuestionSubmitVO;
import com.hutu.hutuojmodel.model.vo.QuestionRankListVO;
import com.hutu.hutuojmodel.model.vo.QuestionSubmitVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author 478234818
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-08-09 15:41:52
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;
    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;



    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断题目是否存在，根据id获取题目
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有此题目");
        }
        //是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        //  设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();

        myMessageProducer.sendMessage("code_exchange","my_routingKey",String.valueOf(questionSubmitId));
//        //异步执行代码
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmitId);
//        });

        return questionSubmitId;
    }

    /**
     * 获取用户自己提交记录
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        //从数据库查询到原始数据的分页信息
        Page<QuestionSubmit> questionSubmitPage = page(new Page<>(current, size), getQueryWrapper(questionSubmitQueryRequest));
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();

        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(this::getQuestionSubmitVO).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public Page<QuestionRankListVO> getUserRankingList(QuestionRankListRequest questionRankListRequest) {
        // 1. 执行原生SQL查询
        List<UserSubmitStatsDTO> statsList = baseMapper.selectTopPassedUsers();

        // 2. 转换为VO
        List<QuestionRankListVO> voList = statsList.stream()
                .map(dto -> new QuestionRankListVO()
                        .setId(dto.getUserId())
                        .setUserName(dto.getUserName())
                        .setAcceptedQuestionNum(dto.getPassedCount())
                        .setSubmitQuestionNum(dto.getTotalSubmit()))
                .collect(Collectors.toList());

        // 3. 构造分页对象（固定前10条）
        Page<QuestionRankListVO> page = new Page<>();
        return page.setRecords(voList)
                .setCurrent(1)
                .setSize(10)
                .setTotal(voList.size());
    }


    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        Integer status = questionSubmitQueryRequest.getStatus();
        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);


        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC), sortField);
        return queryWrapper;
    }

    //questionSubmit转化QuestionSubmitVO
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit) {
        return QuestionSubmitVO.objToVo(questionSubmit);
    }

    @Override
    public Page<CurrentQuestionSubmitVO> listQuestionSubmitVOByUserIdByQuestionId(QuestionSubmitListRequest questionSubmitListRequest) {

        Integer currentPage = questionSubmitListRequest.getCurrentPage();
        Integer pageSize = questionSubmitListRequest.getPageSize();
        Long userId = questionSubmitListRequest.getUserId();
        Long questionId = questionSubmitListRequest.getQuestionId();
        // 检验参数
        if (currentPage < 0 || pageSize < 0 || questionId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 构建分页对象
        Page<QuestionSubmit> questionSubmitPage = new Page<>(currentPage, pageSize);
        // 构建查询条件
        LambdaQueryWrapper<QuestionSubmit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionSubmit::getQuestionId, questionId).eq(QuestionSubmit::getUserId, userId).orderByDesc(QuestionSubmit::getCreateTime);
        questionSubmitPage = page(questionSubmitPage, queryWrapper);
        //转换结果
        List<CurrentQuestionSubmitVO> currentQuestionSubmitVOList = questionSubmitPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        // 构建分页结果
        Page<CurrentQuestionSubmitVO> currentQuestionSubmitVOPage = new Page<>(
                questionSubmitPage.getCurrent(),
                questionSubmitPage.getSize(),
                questionSubmitPage.getTotal());
        currentQuestionSubmitVOPage.setRecords(currentQuestionSubmitVOList);
        return currentQuestionSubmitVOPage;
    }

    // 将 QuestionSubmit 对象转换为 CurrentQuestionSubmitVO 对象
    private CurrentQuestionSubmitVO convertToVO(QuestionSubmit questionSubmit) {
        return CurrentQuestionSubmitVO.objToVo(questionSubmit);
    }
}




