package RequestDemo;

import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import com.ethereal.client.Request.Annotation.Request;
import com.ethereal.client.Request.WebSocket.WebSocketRequest;


public class ServerRequest extends WebSocketRequest {
    @Request
    public Boolean Register(String username, Long id){
        return false;
    }
    @Request
    public Boolean SendSay(Long listener_id, String message){
        return false;
    }
    @Request(invokeType = InvokeTypeFlags.All | InvokeTypeFlags.ReturnRemote)
    public Integer Add(Integer a, Integer b){
        return 23;
    }
}
