package com.stonenotes.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @Author: stonenotes
 * @Date: 2019/6/17
 */
@Component
@Slf4j
public class KafkaReceiver {

    @KafkaListener(topics = "ot-log-tracking")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        log.info("----------------- curentThread =" + Thread.currentThread().getName());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("----------------- record =" + record);
            log.info("----------------- key =" + record.key());
            log.info("------------------ message =" + message);
        }
    }
}
