package com.ethereal.client.Net.Abstract;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Event.ExceptionEvent;
import com.ethereal.client.Core.Event.LogEvent;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Interface.INet;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Abstract.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Net implements INet {
    protected NetConfig config;
    protected String name;
    protected NetType netType;
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();
    //Java没有自带三元组，这里就引用Kotlin了.
    protected HashMap<String, Service> services = new HashMap<>();
    protected HashMap<String, Request> requests = new HashMap<>();
    public Net(String name){
        this.name = name;
    }
    public NetType getNetType() {
        return netType;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public NetConfig getConfig() {
        return config;
    }

    public void setConfig(NetConfig config) {
        this.config = config;
    }

    public HashMap<String, Service> getServices() {
        return services;
    }

    public void setServices(HashMap<String, Service> services) {
        this.services = services;
    }

    public HashMap<String, Request> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Request> requests) {
        this.requests = requests;
    }
    @Override
    public void serverRequestReceiveProcess(ServerRequestModel request) throws java.lang.Exception {
        Method method;
        Service service = services.get(request.getService());
        if(service != null){
            method = service.getMethods().get(request.getMethodId());
            if(method!= null){
                Parameter[] parameterInfos = method.getParameters();
                ArrayList<Object> parameters = new ArrayList<>(parameterInfos.length);
                int i = 0;
                for (Parameter parameterInfo : parameterInfos)
                {
                    AbstractType type;
                    type = service.getTypes().getTypesByType().get(parameterInfo.getParameterizedType());
                    if(type == null)type = service.getTypes().getTypesByName().get(parameterInfo.getAnnotation(com.ethereal.client.Core.Annotation.AbstractType.class).abstractName());
                    if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",parameterInfo.getParameterizedType()));
                    parameters.add(type.getDeserialize().Deserialize(request.getParams()[i]));
                }
                method.invoke(service,parameters.toArray(new Object[]{}));
            }
            else {
                throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s-%s Not Found",name,request.getService(),request.getMethodId()));
            }
        }
        else {
            throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s Not Found",name,request.getService()));
        }
    }
    @Override
    public void clientResponseProcess(ClientResponseModel response) throws TrackException {
        Integer id = Integer.parseInt(response.getId());
        Request request = requests.get(response.getService());
        if(request != null){
            ClientRequestModel requestModel = request.getTasks().get(id);
            if(requestModel != null){
                requestModel.setResult(response);
            }
            else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s-%s RequestId未找到",name,response.getService(),id));
        }
        else onLog(TrackLog.LogCode.Runtime,String.format("%s-%s Request未找到",name,response.getService()));
    }
    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    @Override

    public void onException(TrackException.ErrorCode code, String message){
        onException(new TrackException(code,message));
    }
    @Override
    public void onException(TrackException exception)  {
        exception.setNet(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override
    public void onLog(TrackLog log){
        log.setNet(this);
        logEvent.onEvent(log);
    }
}
