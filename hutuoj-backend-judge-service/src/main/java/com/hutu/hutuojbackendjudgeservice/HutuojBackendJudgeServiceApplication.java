package com.hutu.hutuojbackendjudgeservice;

import com.hutu.hutuojbackendjudgeservice.rabbitmq.MqInitMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author hutu-g
 */
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.hutu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hutu.hutuojbackendserviceclient.service"})
public class HutuojBackendJudgeServiceApplication {

    public static void main(String[] args) throws InterruptedException {
        MqInitMain.doInit();
        SpringApplication.run(HutuojBackendJudgeServiceApplication.class, args);

    }


}
