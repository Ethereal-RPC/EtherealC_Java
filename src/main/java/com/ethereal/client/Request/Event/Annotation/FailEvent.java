package com.ethereal.client.Request.Event.Annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FailEvent {
    String function();
}
