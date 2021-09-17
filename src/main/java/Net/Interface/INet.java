package Net.Interface;

import Core.Interface.IExceptionEvent;
import Core.Interface.ILogEvent;
import Core.Model.ClientResponseModel;
import Core.Model.ServerRequestModel;

public interface INet extends IExceptionEvent, ILogEvent {
    boolean publish() throws Exception;
    void serverRequestReceiveProcess(ServerRequestModel request) throws Exception;
    void clientResponseProcess(ClientResponseModel response) throws Exception;
}
