package com.hutu.hutuojbackendjudgeservice.judge.codesandbox;


import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl.AICodeSandBox;
import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandBox;
import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandBox;
import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandBox;

/**
 * 代码沙箱工厂
 */
public class CodeSandBoxFactory {

    public static CodeSandBox newInstance(String type) {
         switch (type){
             case "example":
                 return new ExampleCodeSandBox();
             case "remote" :
                 return new RemoteCodeSandBox();
             case "thirdParty":
                 return new ThirdPartyCodeSandBox();
             case "ai":
                 return new AICodeSandBox();
             default:
                 return new AICodeSandBox();
         }
    }

}
