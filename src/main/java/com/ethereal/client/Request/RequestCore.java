package com.ethereal.client.Request;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Client.ClientCore;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.Abstract.RequestConfig;
import com.ethereal.client.Request.WebSocket.WebSocketRequestConfig;

import java.lang.reflect.Proxy;

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

    public static <T> T register(Class<?> instance_class,Net net, String serviceName, AbstractTypes type) throws TrackException {
        if(net.getNetType() == NetType.WebSocket){
            return register(instance_class,net,serviceName,new WebSocketRequestConfig(type));
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("未有针对%s的Request-Register处理",net.getNetType()));
    }

    public static <T> T register(Class<?> instance_class,Net net,String serviceName, RequestConfig config) throws TrackException {
        Request request = null;
        request = net.getRequests().get(serviceName);
        if(request == null){
            try{
                request = Request.register((Class<Request>) instance_class,net.getName(),serviceName,config);
                request.getExceptionEvent().register(net::onException);
                request.getLogEvent().register(net::onLog);
                net.getRequests().put(serviceName, request);
            }
            catch (java.lang.Exception err){
                throw new TrackException(TrackException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！", net.getName(),serviceName));
        return (T)request;
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
