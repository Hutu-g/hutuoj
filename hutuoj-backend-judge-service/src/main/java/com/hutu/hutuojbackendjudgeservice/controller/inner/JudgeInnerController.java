package com.hutu.hutuojbackendjudgeservice.controller.inner;

import com.hutu.hutuojbackendjudgeservice.judge.JudgeService;
import com.hutu.hutuojbackendserviceclient.service.JudgeFeignClient;
import com.hutu.hutuojmodel.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户接口（内部）
 *
 * @author hutu-g
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    @PostMapping("/do")
    @Override
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }

}
