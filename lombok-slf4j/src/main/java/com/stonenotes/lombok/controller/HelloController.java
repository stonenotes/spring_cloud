package com.stonenotes.lombok.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
@RestController
public class HelloController {

    @GetMapping("hello")
    public String hello(){
        return "hello world";
    }
}
