package com.stonnotes.logtracking.request;

import lombok.Data;

/**
 * @Author: javan
 * @Date: 2019/6/20
 */
@Data
public class LogSearchRequest {
    private String startTime;
    private String endTime;
    private String message;
    private String level;
    private int pageSize;
    private int pageNum;
}
