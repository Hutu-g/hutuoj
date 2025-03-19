package com.hutu.hutuojmodel.model.codesandbox;

import lombok.Data;

/**
 * AI文心一言（代码沙箱）message
 */
@Data
public class AIMessage {
    private String role = "user";
    private String content;
}
