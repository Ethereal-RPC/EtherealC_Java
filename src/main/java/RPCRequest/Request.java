package RPCRequest;

import Model.*;
import NativeClient.Client;
import RPCRequest.Event.ConnectSuccessEvent;
import RPCRequest.Event.ExceptionEvent;
import RPCRequest.Event.LogEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Request implements InvocationHandler {
    private final ConcurrentHashMap<Integer,ClientRequestModel> tasks = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private String name;
    private String netName;
    private RequestConfig config;
    private Client client;//连接体
    //连接成功事件
    private ConnectSuccessEvent connectSuccessEvent = new ConnectSuccessEvent();


    public ConnectSuccessEvent getConnectSuccessEvent() {
        return connectSuccessEvent;
    }

    public void setConnectSuccessEvent(ConnectSuccessEvent connectSuccessEvent) {
        this.connectSuccessEvent = connectSuccessEvent;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();
    public RequestConfig getConfig() {
        return config;
    }
    public void setConfig(RequestConfig config) {
        this.config = config;
    }
    public ConcurrentHashMap<Integer, ClientRequestModel> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    public static <T> T register(Class<T> interface_class, String netName, String serviceName, RequestConfig config){
        Request proxy = new Request();
        proxy.name = serviceName;
        proxy.netName = netName;
        proxy.config = config;
        return (T)Proxy.newProxyInstance(Request.class.getClassLoader(),new Class<?>[]{interface_class}, proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Annotation.RPCRequest annotation = method.getAnnotation(Annotation.RPCRequest.class);
        if(annotation != null){
            StringBuilder methodId = new StringBuilder(method.getName());
            int param_count;
            if(args!=null)param_count = args.length;
            else param_count = 0;
            String[] array = new String[param_count + 1];
            if(annotation.parameters().length == 0){
                Class<?>[] parameters = method.getParameterTypes();
                for(int i=0,j=1;i<param_count;i++,j++){
                    RPCType rpcType = config.getType().getTypesByType().get(parameters[i]);
                    if(rpcType != null) {
                        methodId.append("-").append(rpcType.getName());
                        array[j] = rpcType.getSerialize().Serialize(args[i]);
                    }
                    else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("Java中的%s类型参数尚未注册！",parameters[i].getName()));
                }
            }
            else {
                String[] types_name = annotation.parameters();
                if(param_count == types_name.length){
                    for(int i=0,j=1;i<args.length;i++,j++){
                        RPCType rpcType = config.getType().getTypesByName().get(types_name[i]);
                        if(rpcType!=null){
                            methodId.append("-").append(rpcType.getName());
                            array[j] = rpcType.getSerialize().Serialize(args[i]);
                        }
                        else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("方法体%s中的抽象类型为%s的类型尚未注册！",method.getName(),types_name[i]));
                    }
                }
                else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("方法体%s中RPCMethod注解与实际参数数量不符,@RPCRequest:%d个,Method:%d个",method.getName(),types_name.length,args.length));
            }
            ClientRequestModel request = new ClientRequestModel("2.0", name, methodId.toString(),array);
            Class<?> return_type = method.getReturnType();
            if(return_type.equals(Void.TYPE)){
                client.send(request);
            }
            else{
                int id = random.nextInt();
                while (tasks.containsKey(id)){
                    id = random.nextInt();
                }
                request.setId(Integer.toString(id));
                tasks.put(id,request);
                try {
                    int timeout = config.getTimeout();
                    if(annotation.timeout() != -1)timeout = annotation.timeout();
                    if(client.send(request)){
                        ClientResponseModel respond = request.getResult(timeout);
                        if(respond != null){
                            if(respond.getError()!=null){
                                throw new RPCException(RPCException.ErrorCode.Runtime,"来自服务端的报错信息：\n" + respond.getError().getMessage());
                            }
                            RPCType rpcType = config.getType().getTypesByName().get(respond.getResultType());
                            if(rpcType!=null){
                                return rpcType.getDeserialize().Deserialize(respond.getResult());
                            }
                            else throw new RPCException(RPCException.ErrorCode.Runtime,respond.getResultType() + "抽象数据类型尚未注册");
                        }
                    }
                }
                finally {
                    tasks.remove(id);
                }
            }
            return null;
        }
        else return method.invoke(this,args);
    }
    public void OnClientException(Exception exception, Client client)  {
        onException(exception);
    }

    public void OnClientLog(RPCLog log, Client client)
    {
        onLog(log);
    }

    public void onException(RPCException.ErrorCode code, String message) throws Exception {
        onException(new RPCException(code,message));
    }
    public void onException(Exception exception)  {
        exceptionEvent.onEvent(exception,this);
    }

    public void onLog(RPCLog.LogCode code, String message){
        onLog(new RPCLog(code,message));
    }

    public void onLog(RPCLog log){
        logEvent.onEvent(log,this);
    }

    public void onConnectSuccess(){
        connectSuccessEvent.onEvent(this);
    }
}
