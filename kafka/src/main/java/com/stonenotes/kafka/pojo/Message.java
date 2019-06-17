package com.stonenotes.kafka.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: javan
 * @Date: 2019/6/17
 */
@Data
public class Message {
    private Long id;    //id
    private String msg; //消息
    private Date sendTime;  //时间戳
}
