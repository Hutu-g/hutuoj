package com.hutu.hutuojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hutu.hutuojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.hutu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hutu.hutuojbackendserviceclient.service"})
public class HutuojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HutuojBackendQuestionServiceApplication.class, args);
    }

}
