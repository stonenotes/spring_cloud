package com.stonenotes.es.annotations;

import java.lang.annotation.*;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface MyField {
    MyFieldType type() default MyFieldType.Auto;

    boolean index() default true;

    String pattern() default "";

    boolean store() default false;

    boolean fielddata() default false;

    String searchAnalyzer() default "";

    String analyzer() default "";

    String normalizer() default "";

    String[] ignoreFields() default {};

    boolean includeInParent() default false;

    String[] copyTo() default {};
}
