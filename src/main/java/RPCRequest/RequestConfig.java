package RPCRequest;

import Model.RPCException;
import Model.RPCLog;
import Model.RPCTypeConfig;
import RPCRequest.Event.ExceptionEvent;
import RPCRequest.Event.LogEvent;

/**
 * @ProjectName: YiXian_Client
 * @Package: com.yixian.material.RPC
 * @ClassName: RPCNetRequestConfig
 * @Description: java类作用描述
 * @Author: Jianxian
 * @CreateDate: 2021/3/5 18:07
 * @UpdateUser: Jianxian
 * @UpdateDate: 2021/3/5 18:07
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RequestConfig {
    private RPCTypeConfig type;
    private int timeout = -1;
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    public void onException(RPCException.ErrorCode code, String message, Request request) throws RPCException {
        onException(new RPCException(code,message),request);
    }
    public void onException(RPCException exception, Request request) throws RPCException {
        exceptionEvent.OnEvent(exception,request);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message, Request request){
        onLog(new RPCLog(code,message),request);
    }
    public void onLog(RPCLog log, Request request){
        logEvent.OnEvent(log,request);
    }
    public RequestConfig(RPCTypeConfig type){
        this.type = type;
    }

    public RPCTypeConfig getType() {
        return type;
    }

    public void setType(RPCTypeConfig type) {
        this.type = type;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
