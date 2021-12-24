package com.liziedu.fake.core;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FakeRequestMapping {

    /**
     * 请求路径
     * @return
     */
    String value();

    /**
     * 请求方法
     * 默认get
     * @return
     */
    FakeRequest.HttpMethod method() default FakeRequest.HttpMethod.GET;
}
