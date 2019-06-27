package com.stonnotes.logtracking.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInfo {

    private String id;
    private long createTime;// 创建时间
    private String ip;// 服务器ip
    private String remoteIp;//客服端ip
    private String app;//服务器应用名
    private String sessionId;//会话id
    private String userId;//用户id
    private String level;//日志级别
    private String thread;//所在线程
    private String className; //日志类名称
    private String message;//内容
    private String stackTrace;//异常内容
    private String sourceType;// 来源类型，是从某个flume过来

}
