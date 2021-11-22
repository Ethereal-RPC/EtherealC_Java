package com.ethereal.client.Request.Abstract;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Core.EventRegister.ExceptionEvent;
import com.ethereal.client.Core.EventRegister.LogEvent;
import com.ethereal.client.Core.Event.EventManager;
import com.ethereal.client.Core.Interface.IBaseIoc;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Request.Annotation.RequestMethod;
import com.ethereal.client.Request.EventRegister.ConnectSuccessEvent;
import com.ethereal.client.Request.Interface.IRequest;
import com.ethereal.client.Service.Abstract.Service;
import net.sf.cglib.proxy.*;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
@com.ethereal.client.Request.Annotation.Request
public abstract class Request implements IRequest, IBaseIoc {
    private final ConcurrentHashMap<Integer,ClientRequestModel> tasks = new ConcurrentHashMap<>();
    protected String name;
    protected Net net;
    protected RequestConfig config;
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();
    protected AbstractTypes types = new AbstractTypes();
    protected Client client;
    private HashMap<String, Service> services = new HashMap<>();
    private EventManager eventManager = new EventManager();
    private HashMap<String,Object> iocContainer = new HashMap<>();

    public AbstractTypes getTypes() {
        return types;
    }

    public void setTypes(AbstractTypes types) {
        this.types = types;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public static Request register(Class<Request> instance_class){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(instance_class);
        RequestInterceptor interceptor = new RequestInterceptor();
        Callback noOp= NoOp.INSTANCE;
        enhancer.setCallbacks(new Callback[]{noOp,interceptor});
        enhancer.setCallbackFilter(method -> {
            if(method.getAnnotation(RequestMethod.class) != null){
                return 1;
            }
            else return 0;
        });
        return (Request)enhancer.create();
    }

    //连接成功事件
    protected ConnectSuccessEvent connectSuccessEvent = new ConnectSuccessEvent();

    public ConnectSuccessEvent getConnectSuccessEvent() {
        return connectSuccessEvent;
    }

    public void setConnectSuccessEvent(ConnectSuccessEvent connectSuccessEvent) {
        this.connectSuccessEvent = connectSuccessEvent;
    }

    public HashMap<String, Service> getServices() {
        return services;
    }

    public void setServices(HashMap<String, Service> services) {
        this.services = services;
    }

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

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
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


    public void clientResponseProcess(ClientResponseModel response) throws TrackException {
        Integer id = Integer.parseInt(response.getId());
        ClientRequestModel requestModel = getTasks().get(id);
        if(requestModel != null){
            requestModel.setResult(response);
        }
        else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s-%s RequestId未找到",name,response.getService(),id));

    }
    @Override

    public void onException(TrackException.ErrorCode code, String message) {
        onException(new TrackException(code,message));
    }
    @Override
    public void onException(TrackException exception)  {
        exception.setRequest(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override
    public void onLog(TrackLog log){
        log.setRequest(this);
        logEvent.onEvent(log);
    }

    public void onConnectSuccess(){
        connectSuccessEvent.onEvent(this);
    }


    @Override
    public void registerIoc(String name, Object instance) throws TrackException {
        if(iocContainer.containsKey(name)){
            throw new TrackException(TrackException.ErrorCode.Runtime, String.format("%s服务已经注册%sIOC实例",this.name,name));
        }
        iocContainer.put(name,instance);
        eventManager.registerEventMethod(name,instance);
    }

    @Override
    public void unregisterIoc(String name) {
        if(iocContainer.containsKey(name)){
            Object instance = iocContainer.get(name);
            iocContainer.remove(name);
            eventManager.unregisterEventMethod(name,instance);
        }
    }

    @Override
    public Object getIocObject(String name) {
        return iocContainer.get(name);
    }
}
