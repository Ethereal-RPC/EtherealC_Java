package com.ethereal.client.Core.Manager.Event.Annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(value = {ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventContextParam {

}

