package RequestDemo;

import Annotation.RPCRequest;


public interface ServerRequest {
    @RPCRequest
    public Boolean Register(String username, Long id);
    @RPCRequest
    public Boolean SendSay(Long listener_id, String message);
    @RPCRequest
    public Integer Add(Integer a, Integer b);
}
