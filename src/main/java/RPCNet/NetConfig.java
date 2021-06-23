package RPCNet;

import Model.RPCException;
import Model.RPCLog;
import NativeClient.SocketClient;
import RPCNet.Event.ExceptionEvent;
import RPCNet.Event.LogEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * @ProjectName: YiXian_Client
 * @Package: com.yixian.material.RPC
 * @ClassName: RPCNetConfig
 * @Description: java类作用描述
 * @Author: Jianxian
 * @CreateDate: 2021/3/5 18:10
 * @UpdateUser: Jianxian
 * @UpdateDate: 2021/3/5 18:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class NetConfig {
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    public void onException(RPCException.ErrorCode code, String message, Net net) throws RPCException {
        onException(new RPCException(code,message),net);
    }
    public void onException(RPCException exception, Net net) throws RPCException {
        exceptionEvent.OnEvent(exception,net);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message, Net net){
        onLog(new RPCLog(code,message),net);
    }
    public void onLog(RPCLog log, Net net){
        logEvent.OnEvent(log,net);
    }
}
