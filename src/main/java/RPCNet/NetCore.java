package RPCNet;

import Model.*;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class NetCore {
    private static HashMap<Pair<String, String>, NetConfig> configs = new HashMap();

    public static NetConfig Get(Pair<String, String> key)
    {
        return configs.get(key);
    }
    public static void register(String ip, String port) throws RPCException {
        register(ip, port, new NetConfig());
    }
    public static void register(String ip, String port, NetConfig config) throws RPCException {
        NetConfig value = configs.get(new Pair<>(ip, port));
        if (value == null)
        {
            if(config.getClientResponseReceive() == null)config.setClientResponseReceive(NetCore::ClientResponseProcess);
            if(config.getServerRequestReceive() == null)config.setServerRequestReceive(NetCore::ServerRequestReceive);
            configs.put(new Pair<>(ip, port), config);
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s的NetConfig已经注册",ip,port));
    }
    public static Boolean unregister(String ip, String port)
    {
        NetConfig config = null;
        return configs.remove(new Pair<String, String>(ip, port),config);
    }
    private static void ServerRequestReceive(String ip, String port, NetConfig config, ServerRequestModel request) throws InvocationTargetException, IllegalAccessException, RPCException {
        Method method;
        Service service = ServiceCore.get(new Triplet<>(ip,port,request.getService()));
        if(service != null){
            method = service.getMethods().get(request.getMethodId());
            if(method!= null){
                //开始序列化参数
                String[] param_id = request.getMethodId().split("-");
                for (int i = 1,j=0; i < param_id.length; i++,j++)
                {
                    RPCType rpcType = service.getTypes().getTypesByName().get(param_id[i]);
                    if(rpcType == null)throw new RPCException(String.format("RPC中的%s类型参数尚未被注册！",param_id[i]));
                    else request.getParams()[j] = rpcType.getDeserialize().Deserialize((String)request.getParams()[j]);
                }
                method.invoke(service.getInstance(),request.getParams());
            }
            else {
                throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s-%s Not Found",ip,port,request.getService(),request.getMethodId()));
            }
        }
        else {
            throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s Not Found",ip,port,request.getService()));
        }
    }
    private static void ClientResponseProcess(String ip, String port, NetConfig netConfig, ClientResponseModel response) throws RPCException {
        int id = Integer.parseInt(response.getId());
        Request request = RequestCore.get(new Triplet<>(ip,port,response.getService()));
        if(request != null){
            ClientRequestModel requestModel = request.getTasks().get(id);
            if(requestModel != null){
                requestModel.setResult(response);
            }
            else throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s-%s RequestId未找到",ip,port,response.getService(),id));
        }
        else throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s Request未找到",ip,port,response.getService()));
    }
}
