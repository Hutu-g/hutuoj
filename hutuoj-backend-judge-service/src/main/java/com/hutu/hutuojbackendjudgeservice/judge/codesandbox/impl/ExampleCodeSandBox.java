package com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl;



import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeRequest;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeResponse;
import com.hutu.hutuojmodel.model.codesandbox.JudgeInfo;
import com.hutu.hutuojmodel.model.enums.JudgeInfoEnum;
import com.hutu.hutuojmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱（例子）
 */
public class ExampleCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("示例代码沙箱");
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("执行成功");
        executeCodeResponse.setState(QuestionSubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        

        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
