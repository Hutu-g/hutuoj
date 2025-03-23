package com.hutu.hutuojbackendquestionservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hutu.hutuojbackendquestionservice.mapper.CommentMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 478234818
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-08-09 15:40:54
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}




