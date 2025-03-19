package com.hutu.hutuojbackendjudgeservice.judge;

import com.hutu.hutuojmodel.model.entity.QuestionSubmit;

/**
 * 判题服务类
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
