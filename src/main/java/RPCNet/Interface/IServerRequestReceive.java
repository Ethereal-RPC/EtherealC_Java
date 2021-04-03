package RPCNet.Interface;

import Model.RPCException;
import Model.ServerRequestModel;
import RPCNet.NetConfig;

import java.lang.reflect.InvocationTargetException;

public interface IServerRequestReceive {
    public void ServerRequestReceive(String ip, String port, NetConfig config, ServerRequestModel request) throws IllegalAccessException, RPCException, InvocationTargetException;
}
