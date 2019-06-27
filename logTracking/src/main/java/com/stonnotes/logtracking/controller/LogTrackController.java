package com.stonnotes.logtracking.controller;

import com.stonnotes.logtracking.pojo.LogInfo;
import com.stonnotes.logtracking.request.LogSearchRequest;
import com.stonnotes.logtracking.result.Result;
import com.stonnotes.logtracking.service.LogTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
@Controller
@RequestMapping(value = "/logTrack", produces = "application/json; charset=UTF-8")
public class LogTrackController {

    @Autowired
    private LogTrackService logTrackService;

//    @RequestMapping("/queryById")
//    @ResponseBody
//    public ResponseEntity<LogInfo> queryById(@RequestParam String id) {
//        LogInfo logInfo = logTrackService.queryById(id);
//        if (logInfo == null) {
//            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
//        }
//        return new ResponseEntity<>(logInfo, HttpStatus.OK);
//    }

    @RequestMapping(value = "/queryById/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<LogInfo> queryById(@PathVariable String id) throws IOException {
        LogInfo logInfo = logTrackService.queryById(id);
        if (logInfo == null) {
            return Result.error("id不存在");
        }
        return Result.success(logInfo);
    }


    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<LogInfo>> query(@RequestBody LogSearchRequest logSearchRequest) throws Exception {
        return logTrackService.query(logSearchRequest);
    }

    @RequestMapping("")
    public String index(Model model){
        model.addAttribute("name", "javan");
        return "index";
    }

    @RequestMapping("/log_detail/{id}")
    public String logDetail(Model model, @PathVariable  String id) throws IOException {
        LogInfo logInfo = logTrackService.queryById(id);
        model.addAttribute("logInfo", logInfo);
        model.addAttribute("message", "12324<br/>asdfsafsa");
        return "log_detail";
    }

}
