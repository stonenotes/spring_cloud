package com.stonnotes.logtracking.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
@Data
public class KafkaLogInfo {

    private String createTime;
    private String level;
    private String thread;
    @JSONField(name = "class")
    private String className;
    private String message;
    @JSONField(name = "stack_trace")
    private String stackTrace;

}
