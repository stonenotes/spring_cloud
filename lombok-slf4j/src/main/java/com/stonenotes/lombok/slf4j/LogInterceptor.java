package com.stonenotes.lombok.slf4j;

import com.stonenotes.lombok.utils.CookieUtils;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author: javan
 * @Date: 2019/6/26
 */
public class LogInterceptor extends HandlerInterceptorAdapter {
    private static final String SESSION_KEY = "sessionId";
    private static final String USER_ID = "userId";
    private static final String APP_NAME = "appName";
    private static final String REMOTE_IP = "remoteIp";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionId = CookieUtils.getCookieValue(request, SESSION_KEY);
        if (StringUtils.isEmpty(sessionId)) {
            sessionId = UUID.randomUUID().toString().replaceAll("-", "");
            CookieUtils.setCookie(request, response, SESSION_KEY, sessionId, 60);
        }
        MDC.put(SESSION_KEY, sessionId);
        MDC.put(USER_ID, "123223211");
        MDC.put(APP_NAME, "lombok-slf4j");
        MDC.put(REMOTE_IP, request.getRemoteHost());
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        MDC.remove(SESSION_KEY);
        MDC.remove(USER_ID);
        MDC.remove(APP_NAME);
        MDC.remove(REMOTE_IP);
    }

}
