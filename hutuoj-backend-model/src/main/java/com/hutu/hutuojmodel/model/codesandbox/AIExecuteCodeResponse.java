package com.hutu.hutuojmodel.model.codesandbox;

import lombok.Data;

/**
 * AI文心一言（代码沙箱）响应信息
 */
@Data
public class AIExecuteCodeResponse {
    private int created;
    private String result;
}
