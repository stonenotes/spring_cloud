package com.stonnotes.logtracking.pojo;

import lombok.Data;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
@Data
public class KafkaLogInfo {

    /**
     * {
     * "createTime":"2019-06-26 17:27:02.395",
     * "ip":"10.21.30.152",
     * "remoteIp":"0:0:0:0:0:0:0:1",
     * "app":"lombok-slf4j",
     * "sessionId":"af1d9f401c004cffb2fe71f587987020",
     * "userId":"123223211",
     * "level":"INFO",
     * "thread":"http-nio-8080-exec-1",
     * "className":"com.stonenotes.lombok.slf4j.LogAspect",
     * "message":"请求地址: http://localhost:8080/hello
     * 相应时间: 4 ms
     * 相应内容：hello world",
     * "stackTrace":""
     * }
     */
    private String createTime;// 创建时间
    private String ip;// 服务器ip
    private String remoteIp;//客服端ip
    private String appName;//服务器应用名
    private String sessionId;//会话id
    private String userId;//用户id
    private String level;//日志级别
    private String thread;//所在线程
    private String loggerName; //日志类名称
    private String message;//内容
    private String thrown;//异常内容

}
