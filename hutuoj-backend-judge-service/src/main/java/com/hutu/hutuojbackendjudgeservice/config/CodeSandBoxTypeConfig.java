package com.hutu.hutuojbackendjudgeservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hutu-g
 * @ createTime 2025-03-28
 * @ description 沙箱调用模式
 * @ version 1.0
 */


@Component
@RefreshScope
public class CodeSandBoxTypeConfig {
    @Value("${codesandbox.type}")
    private String type;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
