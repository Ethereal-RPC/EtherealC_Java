package com.ethereal.client.Request.Abstract;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Core.Annotation.BaseParam;
import com.ethereal.client.Core.BaseCore.MZCore;
import com.ethereal.client.Core.Manager.AbstractType.Param;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Request.Annotation.RequestMapping;
import com.ethereal.client.Request.EventRegister.ConnectSuccessEvent;
import com.ethereal.client.Request.Interface.IRequest;
import com.ethereal.client.Service.Abstract.Service;
import com.ethereal.client.Utils.AnnotationUtils;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@com.ethereal.client.Request.Annotation.Request
public abstract class Request extends MZCore implements IRequest {
    private final ConcurrentHashMap<Integer, ClientRequestModel> tasks = new ConcurrentHashMap<>();
    protected String name;
    protected Net net;
    protected RequestConfig config;
    protected Client client;
    private HashMap<String, Service> services = new HashMap<>();
    //连接成功事件
    protected ConnectSuccessEvent connectSuccessEvent = new ConnectSuccessEvent();
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public static void register(Request instance) throws TrackException {
        for (Method method : instance.getClass().getMethods()){
            RequestMapping requestAnnotation = method.getAnnotation(RequestMapping.class);
            if(requestAnnotation !=null){
                if(method.getReturnType() != void.class){
                    Param paramAnnotation = method.getAnnotation(Param.class);
                    if(paramAnnotation != null){
                        String typeName = paramAnnotation.type();
                        if(instance.getTypes().get(typeName) == null){
                            throw new TrackException(TrackException.ErrorCode.Core, String.format("%s 未提供 %s 抽象类型的映射", method.getName(),typeName));
                        }
                    }
                    else if(instance.getTypes().get(method.getReturnType()) == null){
                        throw new TrackException(TrackException.ErrorCode.Core, String.format("%s 返回值未提供 %s 类型的抽象映射", method.getName(),method.getReturnType()));
                    }
                }
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
            }
        }
    }


    public ConnectSuccessEvent getConnectSuccessEvent() {
        return connectSuccessEvent;
    }

    public void setConnectSuccessEvent(ConnectSuccessEvent connectSuccessEvent) {
        this.connectSuccessEvent = connectSuccessEvent;
    }

    public HashMap<String, Service> getServices() {
        return services;
    }

    public void setServices(HashMap<String, Service> services) {
        this.services = services;
    }

    public RequestConfig getConfig() {
        return config;
    }

    public void setConfig(RequestConfig config) {
        this.config = config;
    }

    public ConcurrentHashMap<Integer, ClientRequestModel> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public void onConnectSuccess(){
        connectSuccessEvent.onEvent(this);
    }
    public void clientResponseProcess(ClientResponseModel response) throws TrackException {
        Integer id = Integer.parseInt(response.getId());
        ClientRequestModel requestModel = getTasks().get(id);
        if (requestModel != null) {
            requestModel.setResult(response);
        } else
            throw new TrackException(TrackException.ErrorCode.Runtime, String.format("%s-%s-%s RequestId未找到", net.getName(), name, id));
    }

}
