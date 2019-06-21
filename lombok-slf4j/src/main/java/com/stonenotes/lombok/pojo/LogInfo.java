package com.stonenotes.lombok.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInfo {
    private long createTime;
    private String level;// ERROR INFO
    private String thread;
    @JSONField(name = "class")
    private String className;
    private String message;
    @JSONField(name = "stack_trace")
    private String stackTrace;
}
