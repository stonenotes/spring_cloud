package com.stonnotes.logtracking.kafka;

import com.alibaba.fastjson.JSON;
import com.stonnotes.logtracking.pojo.KafkaLogInfo;
import com.stonnotes.logtracking.pojo.LogInfo;
import com.stonnotes.logtracking.utils.DataUtil;
import com.stonnotes.logtracking.utils.DateUtil;
import com.stonnotes.logtracking.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Random;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
@Component
@Slf4j
public class KafkaReceiver {

    @Autowired
    private IdWorker idWorker;

    @KafkaListener(topics = "test")
    public void listenerTest(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
//            log.info("------------------key = {}, message = {}",record.toString(), message.toString());
            try {
                KafkaLogInfo kafkaLogInfo = JSON.parseObject(message.toString(), KafkaLogInfo.class);
                if (isFieldEmpty(kafkaLogInfo.getRemoteIp()) && isFieldEmpty(kafkaLogInfo.getSessionId())
                        && isFieldEmpty(kafkaLogInfo.getUserId()) && isFieldEmpty(kafkaLogInfo.getAppName())) {
                    return;
                }
                //long randomTime = new Random().nextInt(60 * 24 * 60 * 60) * 1000L + new Random().nextInt(999);
                long createTime = DateUtil.stringToDate(kafkaLogInfo.getCreateTime()).getTime();
                LogInfo logInfo = new LogInfo();
                logInfo.setId(idWorker.nextId());
                logInfo.setApp(kafkaLogInfo.getAppName());
                logInfo.setIp(kafkaLogInfo.getIp());
                logInfo.setRemoteIp(kafkaLogInfo.getRemoteIp());
                logInfo.setSessionId(kafkaLogInfo.getSessionId());
                logInfo.setUserId(kafkaLogInfo.getUserId());
                logInfo.setClassName(kafkaLogInfo.getLoggerName());
                logInfo.setCreateTime(createTime);
                logInfo.setLevel(kafkaLogInfo.getLevel());
                logInfo.setMessage(kafkaLogInfo.getMessage());
                logInfo.setStackTrace(kafkaLogInfo.getThrown());
                logInfo.setThread(kafkaLogInfo.getThread());
                if (!StringUtils.isEmpty(record.key())) {
                    logInfo.setSourceType(record.key().toString());
                }
                System.out.println(kafkaLogInfo.getCreateTime() + " " + kafkaLogInfo.getSessionId() + " " + kafkaLogInfo.getIp() +
                        " " + kafkaLogInfo.getAppName() + " " + kafkaLogInfo.getMessage() + " " + kafkaLogInfo.getThrown());
                DataUtil.getLogInfoVector().add(logInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isFieldEmpty(String field) {
        return StringUtils.isEmpty(field) || field.contains("ctx");
    }

}
