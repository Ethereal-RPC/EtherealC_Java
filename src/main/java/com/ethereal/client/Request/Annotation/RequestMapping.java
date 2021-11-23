package com.ethereal.client.Request.Annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping{
        String mapping();
        int timeout() default -1;
        int invokeType() default InvokeTypeFlags.Remote | InvokeTypeFlags.ReturnRemote;
}
