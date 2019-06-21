package com.stonenotes.lombok;

import com.alibaba.fastjson.JSON;
import com.stonenotes.lombok.pojo.LogInfo;
import com.stonenotes.lombok.pojo.Person;
import com.stonenotes.lombok.utils.DataUtil;
import com.stonenotes.lombok.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LombokApplication.class)
@Slf4j
public class LombokTest {

    String stackTrace = "java.lang.ArithmeticException: / by zero\n" +
            "at com.stonenotes.lombok.LombokTest.logError(LombokTest.java:45)\n" +
            "at com.stonenotes.lombok.LombokTest.error(LombokTest.java:33)\n" +
            "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
            "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
            "at java.lang.reflect.Method.invoke(Method.java:498)\n" +
            "at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n" +
            "at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n" +
            "at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)\n" +
            "at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n";
    long count = 0;

    @Test
    public void testDate() {
        while (true) {
            count++;
            LogInfo logInfo;
            long randomTime = new Random().nextInt(60 * 24 * 60 * 60) * 1000L + new Random().nextInt(999);
            long createTime = System.currentTimeMillis() - randomTime;
            if (count % 25 == 0) {
                logInfo = new LogInfo(createTime , "ERROR", Thread.currentThread().getName(),
                        "com.stonenotes.lombok.LombokTest", DataUtil.getRandomMessage(), stackTrace);
                log.error(JSON.toJSONString(logInfo));
            } else {
                logInfo = new LogInfo(createTime , "DEBUG", Thread.currentThread().getName(),
                        "com.stonenotes.lombok.LombokTest", DataUtil.getRandomMessage(), stackTrace);
                log.info(JSON.toJSONString(logInfo));
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void error() {
//        log.error("lombok error");
//        log.error("lombok error: {}", "hallo");
//        int random = new Random().nextInt(100);
//        log.info("person object is: {}", new Person(random, "张三丰: " + random));
        int count = 0;
        while (true) {
            count++;
            log.info(DataUtil.getRandomMessage());
            logError(count);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logError(int count) {
        if (count % 25 == 0) {
            try {
                int a = count / 0;
                log.info("a= {}", count);
            } catch (Throwable throwable) {
                log.error("除法错误", throwable);
            }
        }
    }


}
