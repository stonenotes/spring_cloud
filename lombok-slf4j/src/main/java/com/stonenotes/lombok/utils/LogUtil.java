package com.stonenotes.lombok.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: javan
 * @Date: 2019/6/25
 */
@Slf4j
public class LogUtil {

    public static void error(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return;
        HttpServletRequest request = attributes.getRequest();
        String remoteAddr =request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        int remotePort = request.getRemotePort();

        String addr = request.getRequestURI();
        String method = request.getMethod();
        System.out.println(remoteAddr + "\n" + remoteHost + "\n" + remotePort + "\n" + addr + "\n" + method + "\n" + request.getRequestedSessionId());
    }
}
