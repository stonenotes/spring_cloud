package com.stonnotes.logtracking.task;

import com.stonnotes.logtracking.pojo.LogInfo;
import com.stonnotes.logtracking.service.LogTrackService;
import com.stonnotes.logtracking.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/20
 */
@Component
@Slf4j
public class LogTrackTask {

    @Autowired
    private LogTrackService logTrackService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteHistoryData() {
        try {
            logTrackService.deletePeriod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入日志
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void insetLogInfo() {
        System.out.println("size ------------------- " + DataUtil.getLogInfoVector().size());
        if (DataUtil.getLogInfoVector().size() == 0)
            return;
        long lastTime = System.currentTimeMillis();
        System.out.println("start: " + DataUtil.getLogInfoVector().size());
        Iterator<LogInfo> iterator = DataUtil.getLogInfoVector().iterator();
        List<LogInfo> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
            iterator.remove();
        }
        System.out.println("finish: " + DataUtil.getLogInfoVector().size() + "---" + (System.currentTimeMillis() - lastTime));
        System.out.println("finish: " + list.size());
        logTrackService.insert(list);
    }

}
