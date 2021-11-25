package com.ethereal.client.Service;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;
import com.ethereal.client.Service.Abstract.Service;

public class ServiceCore {

    public static <T> T get(String net_name, String request_name,String service_name)  {
        Net net = NetCore.get(net_name);//获取对应的网络节点
        if(net != null){
            Request request = RequestCore.get(net,request_name);//获取对应的网络节点
            if(request!=null){
                return (T) request.getServices().get(service_name);
            }
        }
        return null;
    }
    public static <T> T get(Request request,String serviceName)  {
        return (T)request.getServices().get(serviceName);
    }
    public static <T> T register(Request request,Service service) throws TrackException{
        return register(request,service,null);
    }
    public static <T> T register(Request request,Service service,String serviceName) throws TrackException {
        service.initialize();
        if(serviceName!=null)service.setName(serviceName);
        if(!request.isRegister()){
            request.setRegister(true);
            Service.register(service);
            service.setRequest(request);
            service.getExceptionEvent().register(request::onException);
            service.getLogEvent().register(request::onLog);
            request.getServices().put(service.getName(),service);
            service.register();
            return (T) service;
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！",request.getName(),service.getName()));
    }

    public static boolean unregister(Service service) throws TrackException {
        if(service.isRegister()){
            service.unregister();
            service.getRequest().getServices().remove(service.getName());
            service.setRequest(null);
            service.unInitialize();
            service.setRegister(false);
            return true;
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！",service.getName()));
    }
}
