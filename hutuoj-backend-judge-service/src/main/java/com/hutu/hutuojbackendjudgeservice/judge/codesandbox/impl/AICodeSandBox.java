package com.hutu.hutuojbackendjudgeservice.judge.codesandbox.impl;


//import okhttp3.*;
//import org.json.JSONObject;


import com.hutu.hutuojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeRequest;
import com.hutu.hutuojmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * AI文心一言（代码沙箱）
 */

public class AICodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return null;
    }


////    @Value("${ai-code.api-key}")
//    public static String API_KEY = "GdxAHotGKMeMnIGD0xe7hGSw";
////    @Value("${ai-code.secret-key}")
//    public static String SECRET_KEY = "hI4Q01bFF1r8IjeepnC1yXsWIuBcv8yW";
//    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
//    @Override
//    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
//        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
//        executeCodeResponse.setOutputList(Arrays.asList("{AI判题：outputList不符合需求}"));
//        executeCodeResponse.setState(QuestionSubmitStatusEnum.Failed.getValue());
//        //创建请求
//        AIExecuteCodeRequest aiExecuteCodeRequest = new AIExecuteCodeRequest();
//        AIMessage messages = new AIMessage();
//        List<String> inputList = executeCodeRequest.getInputList();
//        String inputListStr = JSONUtil.toJsonStr(inputList);
//        //todo 缺少对AI进行深度调教
//        String code = executeCodeRequest.getCode();
//        String instruct = "你回答我的格式为：正确or错误，其他不用你回答。假如你是我的java判题老师，以下是我的代码"+code + "以下是我的测试用例" + inputListStr;
//        messages.setContent(instruct);
//        ArrayList<AIMessage> list = ListUtil.toList(messages);
//        aiExecuteCodeRequest.setMessages(list);
//        String content = JSONUtil.toJsonStr(aiExecuteCodeRequest);
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, content);
//        Request request = null;
//        try {
//            request = new Request.Builder()
//                    .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1?access_token=" + getAccessToken())
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//        } catch (IOException e) {
//            throw new BusinessException(500, "请求发送失败");
//        }
//        try {
//            Response response = HTTP_CLIENT.newCall(request).execute();
//            String resStr = response.body().string();
//            AIExecuteCodeResponse aiExecuteCodeResponse = JSONUtil.toBean(resStr, AIExecuteCodeResponse.class);
//            String result = aiExecuteCodeResponse.getResult();
//            executeCodeResponse.setMessage(result);
//            response.close();
//            if (result.equals("正确")){
//                executeCodeResponse.setOutputList(Arrays.asList("{AI判题：outputList符合需求}"));
//                executeCodeResponse.setState(QuestionSubmitStatusEnum.SUCCESS.getValue());
//            }
//        } catch (IOException e) {
//            throw new BusinessException(500, "AI问答失败");
//        }
//
//        return executeCodeResponse;
//    }
//    /**
//     * 从用户的AK，SK生成鉴权签名（Access Token）
//     *
//     * @return 鉴权签名（Access Token）
//     * @throws IOException IO异常
//     */
//    static String getAccessToken() throws IOException {
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
//                + "&client_secret=" + SECRET_KEY);
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/oauth/2.0/token")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        return new JSONObject(response.body().string()).getString("access_token");
//    }

}
