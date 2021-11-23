package com.ethereal.client.Request;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.Abstract.RequestInterceptor;
import com.ethereal.client.Request.Annotation.RequestMapping;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

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
        return register(net,requestClass,null);
    }
    public static <T> T register(Net net, Class<?> requestClass, String serviceName) throws TrackException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(requestClass);
        RequestInterceptor interceptor = new RequestInterceptor();
        Callback noOp= NoOp.INSTANCE;
        enhancer.setCallbacks(new Callback[]{noOp,interceptor});
        enhancer.setCallbackFilter(method -> {
            if(method.getAnnotation(RequestMapping.class) != null){
                return 1;
            }
            else return 0;
        });
        Request request = (Request)enhancer.create();
        request.initialize();
        if(serviceName!=null)request.setName(serviceName);
        if(!net.getRequests().containsKey(request.getName())){
            Request.register(request);
            request.setNet(net);
            request.getExceptionEvent().register(net::onException);
            request.getLogEvent().register(net::onLog);
            net.getRequests().put(request.getName(), request);
            request.register();
            return (T)request;
        }
        else throw new TrackException(TrackException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！", net.getName(),serviceName));
    }

    public static boolean unregister(Request request)  {
        request.unregister();
        request.getNet().getRequests().remove(request.getName());
        request.setNet(null);
        request.unInitialize();
        return true;
    }
}
