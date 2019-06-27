package com.stonenotes.lombok.slf4j;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @Author: javan
 * @Date: 2019/6/26
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com..*.controller..*.*(..))")
    public void log() {

    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容
            System.out.println("\r\n");
            StringBuffer sb = new StringBuffer();
            sb.append("请求地址: " + request.getRequestURL().toString()).append("\r\n");
            sb.append("请求方式: " + request.getMethod()).append("\r\n");
            Enumeration<String> headerNames = request.getHeaderNames();
            sb.append("请求Header: ").append("\r\n");
            while (headerNames.hasMoreElements()) {
                String nextElement = headerNames.nextElement();
                sb.append("  " + nextElement + ":" + request.getHeader(nextElement)).append("\r\n");
                //log.info(nextElement + ":" + request.getHeader(nextElement));
            }
            sb.append("请求参数: " + request.getQueryString()).append("\r\n");
            sb.append("远端ip: " + request.getRemoteAddr()).append("\r\n");
            sb.append("执行的方法: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()).append("\r\n");
            sb.append("方法参数 : " + Arrays.toString(joinPoint.getArgs().clone())).append("\r\n");
            log.info(sb.toString());
        }
    }

    @AfterReturning(returning = "ret", pointcut = "log()")
    public void doAferReturning(Object ret) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        StringBuffer sb = new StringBuffer();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sb.append("请求地址: " + request.getRequestURL().toString()).append("\r\n");
        }
        sb.append("相应时间: " + (System.currentTimeMillis() - startTime.get()) + " ms").append("\r\n");
        sb.append("相应内容：" + ret);
        log.info(sb.toString());
    }
}
