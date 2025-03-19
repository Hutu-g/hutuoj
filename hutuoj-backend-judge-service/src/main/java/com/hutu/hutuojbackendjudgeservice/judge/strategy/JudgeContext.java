package com.hutu.hutuojbackendjudgeservice.judge.strategy;

import com.hutu.hutuojmodel.model.codesandbox.JudgeInfo;
import com.hutu.hutuojmodel.model.dto.question.JudgeCase;
import com.hutu.hutuojmodel.model.entity.Question;
import com.hutu.hutuojmodel.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文 用于传递参数
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<JudgeCase> judgeCaseList;
    private List<String> inputList;
    private List<String> outputList;
    private Question question;
    private QuestionSubmit questionSubmit;
}
