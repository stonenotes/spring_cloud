package com.stonenotes.lombok.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: javan
 * @Date: 2019/6/26
 */
@Controller
public class HtmlController {

    @RequestMapping("index")
    public String index(HttpServletRequest request, HttpServletResponse response, String name) {

        return "index:" + name;
    }
}
