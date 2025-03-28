package com.hutu.hutuojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author hutu-g
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class HutuojBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HutuojBackendGatewayApplication.class, args);
    }

}
