package com.liziedu.fake.core;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FakeHeaders {

    String value() default "";
}
