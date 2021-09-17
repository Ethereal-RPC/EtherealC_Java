package RPCRequest;

import Core.Enums.NetType;
import Core.Model.RPCException;
import Core.Model.RPCTypeConfig;
import NativeClient.ClientCore;
import RPCNet.Abstract.Net;
import RPCNet.NetCore;
import RPCRequest.Abstract.Request;
import RPCRequest.Abstract.RequestConfig;
import RPCRequest.WebSocket.WebSocketRequest;
import RPCRequest.WebSocket.WebSocketRequestConfig;

import java.lang.reflect.Proxy;

public class RequestCore {
    //获取Request代理实体
    public static Request getRequest(String netName, String serviceName)  {
        Net net = NetCore.get(netName);
        if (net == null)
            return null;
        return getRequest(net,serviceName);
    }
    //获取Request代理实体
    public static Request getRequest(Net net, String serviceName)  {
        Object request = net.getRequests().get(serviceName);
        return getRequest(request);
    }
    public static Request getRequest(Object request)  {
        return (Request) Proxy.getInvocationHandler(request);
    }
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

    public static <T> T register(Class<T> interface_class,Net net, String serviceName, RPCTypeConfig type) throws RPCException{
        if(net.getNetType() == NetType.WebSocket){
            return register(interface_class,net,serviceName,new WebSocketRequestConfig(type));
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Request-Register处理",net.getNetType()));
    }

    public static <T> T register(Class<T> interface_class,Net net,String serviceName, RequestConfig config) throws RPCException {
        T request = null;
        request = (T) net.getRequests().get(serviceName);
        if(request == null){
            try{
                if(net.getNetType() == NetType.WebSocket){
                    request = WebSocketRequest.register(interface_class,net.getName(),serviceName,config);
                }
                else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Request-Register处理",net.getNetType()));
                ((Request)Proxy.getInvocationHandler(request)).getExceptionEvent().register(net::onException);
                ((Request)Proxy.getInvocationHandler(request)).getLogEvent().register(net::onLog);
                net.getRequests().put(serviceName, request);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！", net.getName(),serviceName));
        return (T)request;
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if (net == null)
            return true;
        return unregister(net, serviceName);
    }
    public static boolean unregister(Net net,String serviceName)  {
        Request request = getRequest(net,serviceName);
        if(request != null){
            ClientCore.unregister(request);
            net.getRequests().remove(serviceName);

        }
        return true;
    }

}
