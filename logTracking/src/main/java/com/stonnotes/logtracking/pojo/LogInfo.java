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
    private long createTime;
    private String level;// ERROR INFO
    private String thread;
    @JSONField(name = "class")
    private String className;
    private String message;
    @JSONField(name = "stack_trace")
    private String stackTrace;
    @JSONField(name = "source_type")
    private String sourceType;// 来源类型，是从某个flume过来

}
