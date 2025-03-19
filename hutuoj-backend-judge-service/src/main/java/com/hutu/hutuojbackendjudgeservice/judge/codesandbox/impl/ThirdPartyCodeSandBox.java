package com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl;


import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeRequest;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * todo
 * 第三方代码沙箱（go-judge）
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
