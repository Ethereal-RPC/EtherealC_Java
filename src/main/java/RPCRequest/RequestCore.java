package RPCRequest;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import Model.RPCException;
import Model.RPCTypeConfig;
import NativeClient.SocketClient;
import RPCNet.Net;
import RPCNet.NetCore;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public class RequestCore {
    public static <T> T register(Class<T> interface_class, String hostname, String port, String serviceName, RPCTypeConfig type) throws RPCException{
        return register(interface_class,hostname,port,serviceName,new RequestConfig(type));
    }

    public static <T> T register(Class<T> interface_class,  String ip, String port,String serviceName, RequestConfig config) throws RPCException {
        Net net = NetCore.Get(new Pair<>(ip,port));
        if(net == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("{%s}-{%s} Net未找到！", ip,port));
        T service = null;
        service = (T) net.getRequests().get(serviceName);
        if(service == null){
            try{
                SocketClient socketClient = null;
                Pair<String,String> clientKey = new Pair<String,String>(ip,port);
                service = Request.register(interface_class,clientKey,serviceName,config);
                net.getRequests().put(serviceName,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.RegisterError,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s-%s已注册,无法重复注册！", ip,port,serviceName));
        return service;
    }

    public static void unregister(String ip, String port,String serviceName) throws RPCException {
        Net net = NetCore.Get(new Pair<>(ip,port));
        if(net == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("{%s}-{%s} Net未找到！",ip,port));
        net.getRequests().remove(serviceName);
    }
    public static Request get(Triplet<String,String,String> key) throws RPCException {
        Net net = NetCore.Get(new Pair<>(key.getValue0(),key.getValue1()));
        if(net == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("{%s}-{%s} Net未找到！", key.getValue0(),key.getValue1()));
        Object request = net.getRequests().get(key.getValue2());
        return (Request) Proxy.getInvocationHandler(request);
    }
}
