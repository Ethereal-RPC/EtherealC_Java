package RPCNet;

import Model.*;
import RPCNet.Interface.IClientRequestSend;
import RPCNet.Interface.IClientResponseReceive;
import RPCNet.Interface.IServerRequestReceive;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class Net {
    private Pair<String, String> clientKey;
    private NetConfig config;
    private IServerRequestReceive serverRequestReceive;
    private IClientResponseReceive clientResponseReceive;
    private IClientRequestSend clientRequestSend;
    //Java没有自带三元组，这里就引用Kotlin了.
    private HashMap<String, Service> services = new HashMap<>();

    private HashMap<String, Object> requests = new HashMap<>();

    public IServerRequestReceive getServerRequestReceive() {
        return serverRequestReceive;
    }

    public void setServerRequestReceive(IServerRequestReceive serverRequestReceive) {
        this.serverRequestReceive = serverRequestReceive;
    }

    public IClientResponseReceive getClientResponseReceive() {
        return clientResponseReceive;
    }

    public void setClientResponseReceive(IClientResponseReceive clientResponseReceive) {
        this.clientResponseReceive = clientResponseReceive;
    }

    public IClientRequestSend getClientRequestSend() {
        return clientRequestSend;
    }

    public void setClientRequestSend(IClientRequestSend clientRequestSend) {
        this.clientRequestSend = clientRequestSend;
    }

    public Pair<String, String> getClientKey() {
        return clientKey;
    }

    public void setClientKey(Pair<String, String> clientKey) {
        this.clientKey = clientKey;
    }

    public NetConfig getConfig() {
        return config;
    }

    public void setConfig(NetConfig config) {
        this.config = config;
    }

    public HashMap<String, Service> getServices() {
        return services;
    }

    public void setServices(HashMap<String, Service> services) {
        this.services = services;
    }

    public HashMap<String, Object> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Object> requests) {
        this.requests = requests;
    }

    public Net(){
        serverRequestReceive = this::ServerRequestReceiveProcess;
        clientResponseReceive = this::ClientResponseProcess;
    }
    private void ServerRequestReceiveProcess(ServerRequestModel request) throws InvocationTargetException, IllegalAccessException, RPCException {
        Method method;
        Service service = services.get(request.getService());
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
                throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s-%s Not Found",clientKey.getValue0(),clientKey.getValue1(),request.getService(),request.getMethodId()));
            }
        }
        else {
            throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s Not Found",clientKey.getValue0(),clientKey.getValue1(),request.getService()));
        }
    }
    private void ClientResponseProcess(ClientResponseModel response) throws RPCException {
        int id = Integer.parseInt(response.getId());
        Request request = (Request) Proxy.getInvocationHandler(requests.get(response.getService()));
        if(request != null){
            ClientRequestModel requestModel = request.getTasks().get(id);
            if(requestModel != null){
                requestModel.setResult(response);
            }
            else throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s-%s RequestId未找到",clientKey.getValue0(),clientKey.getValue1(),response.getService(),id));
        }
        else throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s Request未找到",clientKey.getValue0(),clientKey.getValue1(),response.getService()));
    }

}
