package Net.NetNode.Request;

import Request.Annotation.Request;
import Net.NetNode.Model.NetNode;

public interface ServerNetNodeRequest {
    @Request
    public NetNode GetNetNode(String servicename);
}
