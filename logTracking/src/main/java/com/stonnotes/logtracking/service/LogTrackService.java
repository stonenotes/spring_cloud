package com.stonnotes.logtracking.service;

import com.stonnotes.logtracking.pojo.LogInfo;
import com.stonnotes.logtracking.request.LogSearchRequest;
import com.stonnotes.logtracking.result.Result;

import java.io.IOException;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/20
 */
public interface LogTrackService {

    /**
     * 查询日志列表
     * @param requestParam
     * @return
     */
    public Result<List<LogInfo>> query(LogSearchRequest requestParam) throws IOException;

    /**
     * 通过id查询日志详情
     *
     * @param id
     * @return
     */
    public LogInfo queryById(String id) throws IOException;

    /**
     * 定期清除log日志
     */
    public void deletePeriod() throws IOException;

    /**
     * 批量插入数据
     *
     * @param list
     */
    public void insert(List<LogInfo> list);

}
