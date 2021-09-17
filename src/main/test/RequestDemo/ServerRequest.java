package RequestDemo;

import Request.Annotation.Request;


public interface ServerRequest {
    @Request
    public Boolean Register(String username, Long id);
    @Request
    public Boolean SendSay(Long listener_id, String message);
    @Request
    public Integer Add(Integer a, Integer b);
}
