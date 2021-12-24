package liziedu.fake.spring.glue;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FakeClientsRegister.class)
public @interface EnableFakeClients {

    /**
     * 扫描包路径
     * @return {"com.xx.x", "com.yy.y"}
     */
    String[] value() default {};

    /**
     * 扫描包路径
     * @return {"com.xx.x", "com.yy.y"}
     */
    String[] basePackages() default {};
}
