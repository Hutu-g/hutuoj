package com.hutu.hutuojbackendjudgeservice.judge;


import com.hutu.hutuojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.hutu.hutuojbackendjudgeservice.judge.strategy.JavaJudgeStrategy;
import com.hutu.hutuojbackendjudgeservice.judge.strategy.JudgeContext;
import com.hutu.hutuojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.hutu.hutuojmodel.model.codesandbox.JudgeInfo;
import com.hutu.hutuojmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 简化调用
 * @author hutu-g
 */
@Service
public class JudgeManager {
    /**
     * 选择对应策略
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
