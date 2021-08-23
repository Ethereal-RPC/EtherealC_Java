package RPCNet.NetNode.Request;

import Annotation.RPCRequest;
import RPCNet.NetNode.Model.NetNode;

public interface ServerNetNodeRequest {
    @RPCRequest
    public NetNode GetNetNode(String servicename);
}
