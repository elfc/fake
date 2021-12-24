package com.liziedu.fake.core;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FakeClient {

    /**
     * beanName
     * @return
     */
    String value() default "";

    /**
     * 请求token 类
     * @return
     */
    Class<? extends AccessToken> accessToken() default AccessToken.class;

    /**
     * 请求地址
     * @return
     */
    String domain();
}
