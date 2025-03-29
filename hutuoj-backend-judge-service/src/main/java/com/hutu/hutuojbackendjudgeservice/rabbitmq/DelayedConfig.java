package com.hutu.hutuojbackendjudgeservice.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建判题延迟队列基于bean的形式
 */
@Configuration
public class DelayedConfig {
    @Bean
    public DirectExchange delayedExchange(){
        //指定交换机类型
        return ExchangeBuilder.directExchange("judge_check_exchange")
                //设置delay属性为true
                .delayed()
                //持久化
                .durable(true)
                .build();
    }
    @Bean
    public Queue delayedQueue(){
        return new Queue("judge_check_queue");
    }
    @Bean
    public Binding delayeBinding(){
        return BindingBuilder.bind(delayedQueue())
                .to(delayedExchange())
                .with("judge_check_delay");
    }
}
