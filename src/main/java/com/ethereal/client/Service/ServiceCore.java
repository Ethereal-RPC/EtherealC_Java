package com.ethereal.client.Service;

import ServiceDemo.ClientService;
import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Abstract.Service;
import com.ethereal.client.Service.Abstract.ServiceConfig;
import com.ethereal.client.Service.WebSocket.WebSocketService;

import java.lang.reflect.InvocationTargetException;

public class ServiceCore {

    public static <T> T get(String netName, String serviceName)  {
        Net net = NetCore.get(netName);
        if(net == null)return null;
        return get(net,serviceName);
    }
    public static <T> T get(Net net,String serviceName)  {
        return (T)net.getServices().get(serviceName);
    }


    public static <T> T register(Service instance,Net net, String serviceName, AbstractTypes type) throws TrackException {
        if(net.getNetType() == NetType.WebSocket){
            return register(instance,net,serviceName,new ServiceConfig(type));
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("未有针对%s的Service-Register处理",net.getNetType()));
    }
    public static <T> T register(Class<?> instanceClass,Net net,String serviceName, AbstractTypes type) throws TrackException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(net.getNetType() == NetType.WebSocket){
            return register((Service) instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,new ServiceConfig(type));
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("未有针对%s的Service-Register处理",net.getNetType()));
    }
    public static <T> T register(Class<?> instanceClass,Net net,String serviceName, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, TrackException {
        return register((Service) instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,config);
    }
    public static <T> T register(Service instance, Net net, String serviceName, ServiceConfig config) throws TrackException {
        Service service = net.getServices().get(serviceName);
        if(service == null){
            try{
                Service.register((Service) instance,net.getName(),config);
                net.getServices().put(serviceName,instance);
                instance.getExceptionEvent().register(net::onException);
                instance.getLogEvent().register(net::onLog);
                return (T) instance;
            }
            catch (java.lang.Exception err){
                throw new TrackException(TrackException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s已注册,无法重复注册！",net.getName(),serviceName));
    }

    public static boolean unregister(String netName,String serviceName) throws TrackException {
        Net net = NetCore.get(netName);
        return unregister(net,serviceName);
    }
    public static boolean unregister(Net net,String serviceName) {
        if(net != null){
            if(net.getServices().containsKey(serviceName)){
                net.getServices().remove(serviceName);
            }
        }
        return true;
    }
}
