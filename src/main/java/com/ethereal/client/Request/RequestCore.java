package com.ethereal.client.Request;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Client.ClientCore;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;

public class RequestCore {
    //获取Request实体
    public static <T> T get(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if (net == null){
            return null;
        }
        else return (T)net.getRequests().get(serviceName);
    }
    //获取Request实体
    public static <T> T get(Net net,String serviceName)  {
        Object request = net.getRequests().get(serviceName);
        return (T)request;
    }

    public static <T> T register(Net net, Class<?> requestClass) throws TrackException {
        return register(net,requestClass,null,null);
    }
    public static <T> T register(Net net, Class<?> requestClass, String serviceName, AbstractTypes types) throws TrackException {
        Request request = Request.register((Class<Request>) requestClass);
        if(serviceName!=null)request.setName(serviceName);
        if(types!=null)request.setTypes(types);
        if(!net.getRequests().containsKey(request.getName())){
            request.setNetName(net.getName());
            request.getExceptionEvent().register(net::onException);
            request.getLogEvent().register(net::onLog);
            net.getRequests().put(request.getName(), request);
            return (T)request;
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！", net.getName(),serviceName));
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if (net == null)
            return true;
        return unregister(net, serviceName);
    }
    public static boolean unregister(Net net,String serviceName)  {
        Request request = get(net,serviceName);
        if(request != null){
            ClientCore.unregister(request);
            net.getRequests().remove(serviceName);

        }
        return true;
    }

}
