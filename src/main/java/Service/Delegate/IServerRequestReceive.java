package Service.Delegate;

import Core.Model.ServerRequestModel;

public interface IServerRequestReceive {
    public void ServerRequestReceive(ServerRequestModel request) throws Exception;
}
