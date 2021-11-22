package com.ethereal.client.Service;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;
import com.ethereal.client.Service.Abstract.Service;

import java.lang.reflect.InvocationTargetException;

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
        return register(request,service,null,null);
    }
    public static <T> T register(Request request,Service service,String serviceName,AbstractTypes types) throws TrackException {
        service.initialize();
        if(serviceName!=null)service.setName(serviceName);
        if(types!=null)service.setTypes(types);
        Service.register(service);
        if(!request.getServices().containsKey(service.getName())){
            service.setRequest(request);
            service.getExceptionEvent().register(request::onException);
            service.getLogEvent().register(request::onLog);
            request.getServices().put(service.getName(),service);
            service.register();
            return (T) service;
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！",request.getName(),service.getName()));
    }

    public static boolean unregister(Service service) {
        service.unregister();
        service.getRequest().getServices().remove(service.getName());
        service.setRequest(null);
        return true;
    }
}
