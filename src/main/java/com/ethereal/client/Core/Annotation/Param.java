package com.ethereal.client.Core.Annotation;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
        String name() default "";
}
