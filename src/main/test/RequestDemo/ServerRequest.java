package RequestDemo;

import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import com.ethereal.client.Request.Annotation.RequestMethod;
import com.ethereal.client.Request.WebSocket.WebSocketRequest;


public class ServerRequest extends WebSocketRequest {
    @RequestMethod
    public Boolean Register(String username, Long id){
        return false;
    }
    @RequestMethod
    public Boolean SendSay(Long listener_id, String message){
        return false;
    }


    @RequestMethod(invokeType = InvokeTypeFlags.All | InvokeTypeFlags.ReturnRemote)
    public Integer Add(Integer a, Integer b){
        return 23;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void unInitialize() {

    }
}
