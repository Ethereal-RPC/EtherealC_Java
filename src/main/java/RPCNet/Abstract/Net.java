package RPCNet.Abstract;

import Core.Enums.NetType;
import Core.Event.ExceptionEvent;
import Core.Event.LogEvent;
import Core.Model.*;
import RPCNet.Delegate.IClientResponseReceive;
import RPCNet.Delegate.IServerRequestReceive;
import RPCNet.Interface.INet;
import RPCRequest.Abstract.Request;
import RPCService.Abstract.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public abstract class Net implements INet {
    protected NetConfig config;
    protected IServerRequestReceive serverRequestReceive;
    protected IClientResponseReceive clientResponseReceive;
    protected String name;
    protected NetType netType;
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();
    //Java没有自带三元组，这里就引用Kotlin了.
    protected HashMap<String, Service> services = new HashMap<>();
    protected HashMap<String, Object> requests = new HashMap<>();

    public NetType getNetType() {
        return netType;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



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
        serverRequestReceive = this::serverRequestReceiveProcess;
        clientResponseReceive = this::clientResponseProcess;
    }
    @Override
    public void serverRequestReceiveProcess(ServerRequestModel request) throws Exception {
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
                    if(rpcType == null){
                        throw new RPCException(RPCException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",param_id[i]));
                    }
                    else request.getParams()[j] = rpcType.getDeserialize().Deserialize((String)request.getParams()[j]);
                }
                method.invoke(service,request.getParams());
            }
            else {
                throw new RPCException(RPCException.ErrorCode.Runtime,String.format("%s-%s-%s Not Found",name,request.getService(),request.getMethodId()));
            }
        }
        else {
            throw new RPCException(RPCException.ErrorCode.Runtime,String.format("%s-%s Not Found",name,request.getService()));
        }
    }
    @Override
    public void clientResponseProcess(ClientResponseModel response) throws RPCException {
        Integer id = Integer.parseInt(response.getId());
        Request request = (Request) Proxy.getInvocationHandler(requests.get(response.getService()));
        if(request != null){
            ClientRequestModel requestModel = request.getTasks().get(id);
            if(requestModel != null){
                requestModel.setResult(response);
            }
            else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("%s-%s-%s RequestId未找到",name,response.getService(),id));
        }
        else onLog(RPCLog.LogCode.Runtime,String.format("%s-%s Request未找到",name,response.getService()));
    }
    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    @Override

    public void onException(RPCException.ErrorCode code, String message){
        onException(new RPCException(code,message));
    }
    @Override
    public void onException(RPCException exception)  {
        exception.setNet(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(RPCLog.LogCode code, String message){
        onLog(new RPCLog(code,message));
    }
    @Override
    public void onLog(RPCLog log){
        log.setNet(this);
        logEvent.onEvent(log);
    }
}
