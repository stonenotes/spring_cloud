package com.stonenotes.log4j2;

import org.apache.logging.log4j.ThreadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: javan
 * @Date: 2019/7/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Log4j2Application.class)
public class Log4j2Test {
    private static final Logger log = LoggerFactory.getLogger(Log4j2Test.class);

    @Test
    public void testInfo() {

        for (int i = 0; i < 1; i++) {
            MDC.put("userId", "123456");
//            ThreadContext.put("userId", "item: " + i);
            log.info("hello info " + i);
//            ThreadContext.remove("userId");
            MDC.remove("userId");
        }

    }

    @Test
    public void testError() {
        MDC.put("userId", "1234567");
        log.error("NullPointerException error", new NullPointerException("错误信息，空指针"));
        MDC.remove("userId");
    }
}
