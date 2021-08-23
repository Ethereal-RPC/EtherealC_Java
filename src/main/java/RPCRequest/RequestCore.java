package RPCRequest;
import java.lang.reflect.Proxy;

import Model.RPCException;
import Model.RPCTypeConfig;
import NativeClient.SocketClient;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.Event.Delegate.OnExceptionDelegate;

public class RequestCore {

    public static Request get(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if (net == null)
            return null;
        return get(net,serviceName);
    }
    public static Request get(Net net,String serviceName)  {
        Object request = net.getRequests().get(serviceName);
        return (Request) Proxy.getInvocationHandler(request);
    }

    public static <T> T register(Class<T> interface_class,Net net, String serviceName, RPCTypeConfig type) throws RPCException{
        return register(interface_class,net,serviceName,new RequestConfig(type));
    }

    public static <T> T register(Class<T> interface_class,Net net,String serviceName, RequestConfig config) throws RPCException {
        T request = null;
        request = (T) net.getRequests().get(serviceName);
        if(request == null){
            try{
                request = Request.register(interface_class,net.getName(),serviceName,config);
                ((Request)request).getExceptionEvent().register(net::OnRequestException);
                ((Request)request).getLogEvent().register(net::OnRequestLog);
                net.getRequests().put(serviceName, request);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！", net.getName(),serviceName));
        return request;
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if (net == null)
            return true;
        return unregister(net, serviceName);
    }
    public static boolean unregister(Net net,String serviceName)  {
        net.getRequests().remove(serviceName);
        return true;
    }

}
