package com.hutu.hutuojmodel.model.codesandbox;

import lombok.Data;

import java.util.List;

/**
 * AI文心一言（代码沙箱）请求信息
 */
@Data
public class AIExecuteCodeRequest {
    private List<AIMessage> messages;
    private float top_p = 0.1F;
    private String user_id;
//    private String disable_search = "false";
//    private String enable_citation = "false";
}
