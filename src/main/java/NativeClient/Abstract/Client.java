package NativeClient.Abstract;

import Core.Event.ExceptionEvent;
import Core.Event.LogEvent;
import Core.Model.RPCException;
import Core.Model.RPCLog;
import NativeClient.Event.ConnectEvent;
import NativeClient.Event.DisConnectEvent;
import NativeClient.Interface.IClient;

/**
 * @ProjectName: YiXian
 * @Package: com.xianyu.yixian.Core.Model
 * @ClassName: EchoClient
 * @Description: TCP客户端
 * @Author: Jianxian
 * @CreateDate: 2020/11/16 20:17
 * @UpdateUser: Jianxian
 * @UpdateDate: 2020/11/16 20:17
 * @UpdateRemark: 类的第一次生成
 * @Version: 1.0
 */
public abstract class Client implements IClient {
    protected ClientConfig config;
    protected String netName;
    protected String serviceName;
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();
    protected ConnectEvent connectEvent = new ConnectEvent();
    protected DisConnectEvent disConnectEvent = new DisConnectEvent();

    public ConnectEvent getConnectEvent() {
        return connectEvent;
    }

    public void setConnectEvent(ConnectEvent connectEvent) {
        this.connectEvent = connectEvent;
    }

    public DisConnectEvent getDisConnectEvent() {
        return disConnectEvent;
    }

    public void setDisConnectEvent(DisConnectEvent disConnectEvent) {
        this.disConnectEvent = disConnectEvent;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public ClientConfig getConfig() {
        return config;
    }

    public void setConfig(ClientConfig config) {
        this.config = config;
    }
    @Override
    public void onException(RPCException.ErrorCode code, String message){
        onException(new RPCException(code, message));
    }
    @Override
    public void onException(RPCException exception)  {
        exception.setClient(this);
        exceptionEvent.onEvent(exception);
    }
    @Override
    public void onLog(RPCLog.LogCode code, String message) {
        onLog(new RPCLog(code, message));
    }
    @Override
    public void onLog(RPCLog log) {
        log.setClient(this);
        logEvent.onEvent(log);
    }

    public void onConnectSuccess() {
        connectEvent.onEvent(this);
    }

    public void onDisConnectEvent() {
        disConnectEvent.onEvent(this);
    }
}
