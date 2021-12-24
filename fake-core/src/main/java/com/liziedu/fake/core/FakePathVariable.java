package com.liziedu.fake.core;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FakePathVariable {

    /**
     * 参数值
     * @return
     */
    String value() default "";
}
