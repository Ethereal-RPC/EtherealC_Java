package RPCService.Interface;

import Core.Interface.IExceptionEvent;
import Core.Interface.ILogEvent;
import RPCService.ServiceConfig;

public interface IService extends IExceptionEvent, ILogEvent {
    public void register(Object instance,String netName, ServiceConfig config) throws Exception;
}
