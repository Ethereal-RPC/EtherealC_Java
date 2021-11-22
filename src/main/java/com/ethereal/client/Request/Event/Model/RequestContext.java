package com.ethereal.client.Request.Event.Model;

import com.ethereal.client.Core.Event.Model.EventContext;

import java.lang.reflect.Method;
import java.util.HashMap;

public class RequestContext extends EventContext {

    public RequestContext(HashMap<String, Object> parameters, Method method) {
        super(parameters, method);
    }
}
