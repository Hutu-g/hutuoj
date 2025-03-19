package com.hutu.hutuojbackendjudgeservice.judge.codesandbox;


import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeRequest;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandBox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
