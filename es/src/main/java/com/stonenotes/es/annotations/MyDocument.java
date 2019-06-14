package com.stonenotes.es.annotations;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MyDocument {
    String indexName();

    String type() default "";

    boolean useServerConfiguration() default false;

    short shards() default 5;

    short replicas() default 1;
}
