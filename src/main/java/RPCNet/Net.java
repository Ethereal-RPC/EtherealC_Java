package RPCNet;

import Model.*;
import NativeClient.ClientConfig;
import NativeClient.ClientCore;
import NativeClient.Event.Delegate.OnConnectFailDelegate;
import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.SocketClient;
import RPCNet.Event.ExceptionEvent;
import RPCNet.Event.LogEvent;
import RPCNet.Interface.IClientResponseReceive;
import RPCNet.Interface.IServerRequestReceive;
import RPCNet.NetNode.Model.NetNode;
import RPCNet.NetNode.Request.ServerNetNodeRequest;
import RPCNet.NetNode.Service.ClientNetNodeService;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import org.javatuples.Triplet;
import sun.util.resources.cldr.ewo.CalendarData_ewo_CM;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Net {
    private NetConfig config;
    private IServerRequestReceive serverRequestReceive;
    private IClientResponseReceive clientResponseReceive;
    private String name;
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();
    private Semaphore connectSign = new Semaphore(1);

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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
    private void ServerRequestReceiveProcess(ServerRequestModel request) throws Exception {
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
                        service.onException(new RPCException(RPCException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",param_id[i])));
                    }
                    else request.getParams()[j] = rpcType.getDeserialize().Deserialize((String)request.getParams()[j]);
                }
                method.invoke(service,request.getParams());
            }
            else {
                service.onException(new RPCException(RPCException.ErrorCode.Runtime,String.format("%s-%s-%s Not Found",name,request.getService(),request.getMethodId())));
            }
        }
        else {
            onException(new RPCException(RPCException.ErrorCode.Runtime,String.format("%s-%s Not Found",name,request.getService())));
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
            else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("%s-%s-%s-%s RequestId未找到",name,response.getService(),id));
        }
        onLog(RPCLog.LogCode.Runtime,String.format("%s-%s Request未找到",name,response.getService()));
    }
    public boolean publish() throws RPCException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        //分布式模式
        if(config.getNetNodeMode()){
            //注册数据类型
            RPCTypeConfig types = new RPCTypeConfig();
            types.add(Integer.class,"Int");
            types.add(Long.class,"Long");
            types.add(String.class,"String");
            types.add(Boolean.class,"Bool");
            types.add(NetNode.class,"NetNode");
            //注册网关
            Net net = NetCore.register(String.format("NetNode-%s", name));
            net.logEvent.register((log, net1) -> onLog(log));
            net.exceptionEvent.register(((exception, net1) -> {
                try {
                    onException(exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            //注册服务
            Service netNodeService = ServiceCore.register(ClientNetNodeService.class,net,"ClientNetNodeService",types);
            //注册请求
            ServerNetNodeRequest netNodeRequest = RequestCore.register(ServerNetNodeRequest.class,net,"ServerNetNodeService",types);
            new Thread(() -> {
                while (true){
                    try {
                        NetNodeSearch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(config.getNetNodeHeartInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        else {
            for(Object _request : requests.values()){
                Request request = (Request)_request;
                request.getClient().start();
            }
        }
        return true;
    }

    private void NetNodeSearch() throws Exception {
        synchronized (connectSign){
            boolean flag = false;
            for(Object _request : requests.values()){
                Request request = (Request)_request;
                if(request.getClient() == null){
                    flag = true;
                    break;
                }
            }
            if(flag){
                SocketClient client = null;
                Net net = NetCore.get(String.format("NetNode-%s", name));
                if(net==null)onException(RPCException.ErrorCode.Runtime, String.format("NetNode-%s 未找到", name));
                for (Triplet<String ,String , ClientConfig> item: config.getNetNodeIps()) {
                    String ip = item.getValue0();
                    String port = item.getValue1();
                    ClientConfig config = item.getValue2();
                    client = ClientCore.register(net,"ServerNetNodeService",ip,port,config);
                    if(client == null)onException(RPCException.ErrorCode.Runtime, String.format("%s-%s 服务未找到", net.name,"ServerNetNodeService"));
                    net.config.setNetNodeMode(false);
                    client.getConnectSuccessEvent().register(new OnConnectSuccessDelegate() {
                        @Override
                        public void OnConnectSuccess(SocketClient client) {
                            connectSign.release();
                            client.getConnectSuccessEvent().unRegister(this);
                        }
                    });
                    client.getConnectFailEvent().register(new OnConnectFailDelegate() {
                        @Override
                        public void OnConnectFail(SocketClient client) {
                            connectSign.release();
                            client.getConnectFailEvent().unRegister(this);
                        }
                    });
                    //启动连接
                    client.start();
                    connectSign.acquire();
                    //连接成功
                    if(client.getChannel().isActive()){
                        break;
                    }
                    else {
                        ClientCore.unregister(net,"ServerNetNodeService");
                    }
                }
                if(client.getChannel().isActive()){
                    Request netNodeRequest = RequestCore.get(net,"ServerNetNodeService");
                    if(netNodeRequest != null)onException(RPCException.ErrorCode.Runtime,String.format("%s-%s 查找不到该请求", name,"ServerNetNodeService"));
                    for (Object _request : requests.values()) {
                        Request request = (Request) _request;
                        if(request.getClient() == null){
                            //获取服务节点
                            NetNode node = ((ServerNetNodeRequest)netNodeRequest).GetNetNode("ServerNetNodeService");
                            if(node != null){
                                //注册连接并启动连接
                                SocketClient requestClient = ClientCore.register(request,node.getIp(),node.getPort());
                                requestClient.getConnectFailEvent().register(new OnConnectFailDelegate() {
                                    @Override
                                    public void OnConnectFail(SocketClient client) throws Exception {
                                        client.getConnectFailEvent().unRegister(this);
                                        ClientCore.unregister(client.getNetName(),client.getServiceName());
                                        NetNodeSearch();
                                    }
                                });
                                requestClient.start();
                            }
                            else onException(RPCException.ErrorCode.Runtime, String.format("%s-%s 在NetNode分布式节点中未找到节点", name,request.getName()));
                        }
                    }
                }
                ClientCore.unregister(net,"ServerNetNodeService");
            }
        }

    }

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    public void onException(RPCException.ErrorCode code, String message) throws Exception {
        onException(new RPCException(code,message));
    }
    public void onException(Exception exception) throws Exception {
        exceptionEvent.OnEvent(exception,this);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message){
        onLog(new RPCLog(code,message));
    }
    public void onLog(RPCLog log){
        logEvent.OnEvent(log,this);
    }

    public void OnRequestException(Exception exception, Request request) throws Exception {
        onException(exception);
    }

    public void OnRequestLog(RPCLog log, Request request)
    {
        onLog(log);
    }

    public void OnServiceException(Exception exception, Service service) throws Exception {
        onException(exception);
    }

    public void OnServiceLog(RPCLog log, Service service)
    {
        onLog(log);
    }
}
