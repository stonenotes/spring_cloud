package com.stonenotes.lombok.controller;

import com.stonenotes.lombok.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
@RestController
@Slf4j
public class HelloController {

    @GetMapping("hello")
    public String hello(){
        // LogUtil.error();
        log.debug("debug==================");
//        log.warn("warn===============");
        return "hello world";
    }

    @GetMapping("hello2")
    public String hello2(String name){
//         LogUtil.error();
        log.error("hello2 error");
        log.error("hello2 error2:{}", new NullPointerException(
                "NullPointerException...."
        ));
        return "hello: " + name;
    }
}
