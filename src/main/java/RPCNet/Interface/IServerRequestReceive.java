package RPCNet.Interface;

import Model.RPCException;
import Model.ServerRequestModel;
import RPCNet.NetConfig;

import java.lang.reflect.InvocationTargetException;

public interface IServerRequestReceive {
    public void ServerRequestReceive(ServerRequestModel request) throws IllegalAccessException, RPCException, InvocationTargetException;
}
