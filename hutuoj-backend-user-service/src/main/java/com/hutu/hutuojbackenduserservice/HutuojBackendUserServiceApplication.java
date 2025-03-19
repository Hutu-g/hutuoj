package com.hutu.hutuojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("com.hutu.hutuojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.hutu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hutu.hutuojbackendserviceclient.service"})
public class HutuojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HutuojBackendUserServiceApplication.class, args);
    }

}
