package com.hutu.hutuojbackendjudgeservice.rabbitmq;

import com.hutu.hutuojbackendjudgeservice.judge.JudgeService;
import com.hutu.hutuojbackendserviceclient.service.QuestionFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author hutu-g
 * @ createTime 2025-03-11
 * @ description 消息队列消费者
 * @ version 1.0
 */
@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @Resource
    private QuestionFeignClient questionFeignClient;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "judge_check_exchange", type = "x-delayed-message", delayed = "true"),
            value = @Queue(name = "judge_check_queue", durable = "true"),
            key = "judge_check_delay"
    ), ackMode = "MANUAL"
    )
    public void judgeDelayMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        try {
            //3分钟后接收到消息之后检查判题订单是否完成
            if (questionFeignClient.checkQuestionSubmitById(questionSubmitId)){
                channel.basicAck(deliveryTag, false);
            }
            else {
                channel.basicNack(deliveryTag, false, false);
            }

        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }


}
