package com.ethereal.client.Core.Annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AbstractType {
        String abstractName() default "";
}
