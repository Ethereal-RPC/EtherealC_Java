package Annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCRequest {
        String[] parameters() default {};
        int timeout() default -1;
}
