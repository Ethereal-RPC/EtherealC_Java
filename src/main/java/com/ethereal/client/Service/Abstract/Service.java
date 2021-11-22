package com.ethereal.client.Service.Abstract;

import com.ethereal.client.Core.Annotation.Param;
import com.ethereal.client.Core.Event.Annotation.AfterEvent;
import com.ethereal.client.Core.Event.Annotation.BeforeEvent;
import com.ethereal.client.Core.Event.Model.AfterEventContext;
import com.ethereal.client.Core.Event.Model.BeforeEventContext;
import com.ethereal.client.Core.Event.Model.EventContext;
import com.ethereal.client.Core.Event.Model.ExceptionEventContext;
import com.ethereal.client.Core.EventRegister.ExceptionEvent;
import com.ethereal.client.Core.EventRegister.LogEvent;
import com.ethereal.client.Core.Event.EventManager;
import com.ethereal.client.Core.Interface.IBaseIoc;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Annotation.ServiceMethod;
import com.ethereal.client.Service.Interface.IService;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
@com.ethereal.client.Service.Annotation.Service
public abstract class Service implements IService, IBaseIoc {
    private  HashMap<String,Method> methods = new HashMap<>();
    private  ExceptionEvent exceptionEvent = new ExceptionEvent();
    private  LogEvent logEvent = new LogEvent();
    private EventManager eventManager = new EventManager();
    private HashMap<String,Object> iocContainer = new HashMap<>();
    protected  AbstractTypes types = new AbstractTypes();
    protected  Request request;
    protected  String name;
    protected   ServiceConfig config;

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public HashMap<String, Object> getIocContainer() {
        return iocContainer;
    }

    public void setIocContainer(HashMap<String, Object> iocContainer) {
        this.iocContainer = iocContainer;
    }

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

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    public AbstractTypes getTypes() {
        return types;
    }

    public void setTypes(AbstractTypes types) {
        this.types = types;
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


    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    @Override

    public void onException(TrackException.ErrorCode code, String message) {
        onException(new TrackException(code,message));
    }
    @Override

    public void onException(TrackException exception){
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override

    public void onLog(TrackLog log){
        logEvent.onEvent(log);
    }

    public static void register(Service service) throws TrackException {
        //反射 获取类信息=>字段、属性、方法
        for(Method method : service.getClass().getMethods())
        {
            int modifier = method.getModifiers();
            ServiceMethod annotation = method.getAnnotation(ServiceMethod.class);
            if(annotation!=null){
                if(!Modifier.isInterface(modifier)){
                    Parameter[] parameterInfos = method.getParameters();
                    for(Parameter parameterInfo : parameterInfos){
                        Param paramAnnotation = method.getAnnotation(Param.class);
                        AbstractType type = null;
                        if(paramAnnotation != null) type = service.getTypes().getTypesByName().get(paramAnnotation.name());
                        if(type == null)type = service.getTypes().getTypesByType().get(parameterInfo.getParameterizedType());
                        if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",parameterInfo.getParameterizedType()));
                    }
                    service.methods.put(annotation.mapping(),method);
                }
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
        request.setParams(new String[parameterInfos.length]);
        HashMap<String,Object> params = new HashMap<>(parameterInfos.length);
        for(int i = 0; i< parameterInfos.length; i++){
            AbstractType type = types.getTypesByName().get(method.getAnnotation(Param.class).name());
            if(type == null)type = types.getTypesByType().get(parameterInfos[i].getParameterizedType());
            if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",parameterInfos[i].getParameterizedType()));
            request.getParams()[i] = type.getSerialize().Serialize(request.getParams()[i]);
            params.put(parameterInfos[i].getName(),request.getParams()[i++]);
        }
        BeforeEvent beforeEvent = method.getAnnotation(BeforeEvent.class);
        if(beforeEvent != null){
            eventContext = new BeforeEventContext(params,method);
            String iocObjectName = beforeEvent.function().substring(0, beforeEvent.function().indexOf("."));
            eventManager.invokeEvent(getIocObject(iocObjectName), beforeEvent.function(), params,eventContext);
        }
        Object localResult = null;
        try{
            localResult = method.invoke(this,request.getParams());
        }
        catch (Exception e){
            com.ethereal.client.Core.Event.Annotation.ExceptionEvent exceptionEvent = method.getAnnotation(com.ethereal.client.Core.Event.Annotation.ExceptionEvent.class);
            if(exceptionEvent != null){
                eventContext = new ExceptionEventContext(params,method,e);
                String iocObjectName = exceptionEvent.function().substring(0, exceptionEvent.function().indexOf("."));
                eventManager.invokeEvent(getIocObject(iocObjectName), exceptionEvent.function(),params,eventContext);
                if(exceptionEvent.isThrow())throw e;
            }
            else throw e;
        }
        AfterEvent afterEvent = method.getAnnotation(AfterEvent.class);
        if(afterEvent != null){
            eventContext = new AfterEventContext(params,method, localResult);
            String iocObjectName = afterEvent.function().substring(0,afterEvent.function().indexOf("."));
            eventManager.invokeEvent(getIocObject(iocObjectName), afterEvent.function(), params,eventContext);
        }
    }

    @Override
    public void registerIoc(String name, Object instance) throws TrackException {
        if(iocContainer.containsKey(name)){
            throw new TrackException(TrackException.ErrorCode.Runtime, String.format("%s服务已经注册%sIOC实例",this.name,name));
        }
        iocContainer.put(name,instance);
        eventManager.registerEventMethod(name,instance);
    }

    @Override
    public void unregisterIoc(String name) {
        if(iocContainer.containsKey(name)){
            Object instance = iocContainer.get(name);
            iocContainer.remove(name);
            eventManager.unregisterEventMethod(name,instance);
        }
    }

    @Override
    public Object getIocObject(String name) {
        return iocContainer.get(name);
    }
}
