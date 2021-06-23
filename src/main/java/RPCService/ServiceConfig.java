package RPCService;

import Model.RPCException;
import Model.RPCLog;
import Model.RPCTypeConfig;
import RPCService.Event.ExceptionEvent;
import RPCService.Event.LogEvent;

/**
 * @ProjectName: YiXian_Client
 * @Package: com.yixian.material.RPC
 * @ClassName: RPCNetServiceConfig
 * @Description: java类作用描述
 * @Author: Jianxian
 * @CreateDate: 2021/3/5 17:47
 * @UpdateUser: Jianxian
 * @UpdateDate: 2021/3/5 17:47
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ServiceConfig {
    private RPCTypeConfig types;
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    public void onException(RPCException.ErrorCode code, String message, Service service) throws RPCException {
        onException(new RPCException(code,message),service);
    }
    public void onException(RPCException exception, Service service) throws RPCException {
        exceptionEvent.OnEvent(exception,service);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message, Service service){
        onLog(new RPCLog(code,message),service);
    }
    public void onLog(RPCLog log, Service service){
        logEvent.OnEvent(log,service);
    }
    public RPCTypeConfig getTypes() {
        return types;
    }

    public void setTypes(RPCTypeConfig types) {
        this.types = types;
    }

    public ServiceConfig(RPCTypeConfig type) {
        this.types = type;
    }
}
