package com.ethereal.client.Service.Abstract;

import com.ethereal.client.Core.Annotation.BaseParam;
import com.ethereal.client.Core.BaseCore.MZCore;
import com.ethereal.client.Core.Manager.AbstractType.AbstractType;
import com.ethereal.client.Core.Manager.AbstractType.Param;
import com.ethereal.client.Core.Manager.Event.Annotation.AfterEvent;
import com.ethereal.client.Core.Manager.Event.Annotation.BeforeEvent;
import com.ethereal.client.Core.Manager.Event.Model.AfterEventContext;
import com.ethereal.client.Core.Manager.Event.Model.BeforeEventContext;
import com.ethereal.client.Core.Manager.Event.Model.EventContext;
import com.ethereal.client.Core.Manager.Event.Model.ExceptionEventContext;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Annotation.ServiceMapping;
import com.ethereal.client.Service.Interface.IService;
import com.ethereal.client.Utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
@com.ethereal.client.Service.Annotation.Service
public abstract class Service extends MZCore implements IService {
    private  HashMap<String,Method> methods = new HashMap<>();
    protected  Request request;
    protected  String name;
    protected  ServiceConfig config;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Method> getMethods() {
        return methods;
    }
    public void setMethods(HashMap<String, Method> methods) {
        this.methods = methods;
    }

    public ServiceConfig getConfig() {
        return config;
    }

    public void setConfig(ServiceConfig config) {
        this.config = config;
    }

    public static void register(Service instance) throws TrackException {
        for (Method method : instance.getClass().getMethods()){
            ServiceMapping requestAnnotation = method.getAnnotation(ServiceMapping.class);
            if(requestAnnotation !=null){
                for (Parameter parameter : method.getParameters()){
                    if(AnnotationUtils.getAnnotation(parameter, BaseParam.class) != null){
                        continue;
                    }
                    Param paramAnnotation = method.getAnnotation(Param.class);
                    if(paramAnnotation != null){
                        String typeName = paramAnnotation.type();
                        if(instance.getTypes().get(typeName) == null){
                            throw new TrackException(TrackException.ErrorCode.Core, String.format("%s-%s-%s抽象类型未找到",instance.getName() ,method.getName(),paramAnnotation.type()));
                        }
                    }
                    else if(instance.getTypes().get(parameter.getParameterizedType()) == null){
                        throw new TrackException(TrackException.ErrorCode.Core, String.format("%s-%s-%s类型映射抽象类型",instance.getName() ,method.getName(),parameter.getParameterizedType()));
                    }
                }
                instance.methods.put(requestAnnotation.mapping(),method);
            }
        }
    }
    public void serverRequestReceiveProcess(ServerRequestModel request) throws java.lang.Exception {
        Method method = methods.get(request.getMapping());
        if(method == null){
            throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s-%s Not Found",name,request.getService(),request.getMapping()));
        }
        EventContext eventContext;
        Parameter[] parameterInfos = method.getParameters();
        HashMap<String, Object> params = new HashMap<>(parameterInfos.length);
        Object[] args = new Object[parameterInfos.length];
        int idx = 0;
        for(Parameter parameterInfo : parameterInfos){
            if(request.getParams().containsKey(parameterInfo.getName())){
                String value = request.getParams().get(parameterInfo.getName());
                AbstractType type = getTypes().get(parameterInfo);
                args[idx] = type.getDeserialize().Deserialize(value);
            }
            else throw new TrackException(TrackException.ErrorCode.Runtime,
                        String.format("%s实例中%s方法的%s参数未提供注入方案",name,method.getName(),parameterInfo.getName()));
            params.put(parameterInfo.getName(), args[idx++]);
        }
        BeforeEvent beforeEvent = method.getAnnotation(BeforeEvent.class);
        if(beforeEvent != null){
            eventContext = new BeforeEventContext(params,method);
            String iocObjectName = beforeEvent.function().substring(0, beforeEvent.function().indexOf("."));
            iocManager.invokeEvent(iocManager.get(iocObjectName), beforeEvent.function(), params,eventContext);
        }
        Object localResult = null;
        try{
            localResult = method.invoke(this,request.getParams());
        }
        catch (Exception e){
            com.ethereal.client.Core.Manager.Event.Annotation.ExceptionEvent exceptionEvent = method.getAnnotation(com.ethereal.client.Core.Manager.Event.Annotation.ExceptionEvent.class);
            if(exceptionEvent != null){
                eventContext = new ExceptionEventContext(params,method,e);
                String iocObjectName = exceptionEvent.function().substring(0, exceptionEvent.function().indexOf("."));
                iocManager.invokeEvent(iocManager.get(iocObjectName), exceptionEvent.function(),params,eventContext);
                if(exceptionEvent.isThrow())throw e;
            }
            else throw e;
        }
        AfterEvent afterEvent = method.getAnnotation(AfterEvent.class);
        if(afterEvent != null){
            eventContext = new AfterEventContext(params,method, localResult);
            String iocObjectName = afterEvent.function().substring(0,afterEvent.function().indexOf("."));
            iocManager.invokeEvent(iocManager.get(iocObjectName), afterEvent.function(), params,eventContext);
        }
    }
}
