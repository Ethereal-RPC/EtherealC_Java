package RequestDemo;

import Model.User;
import ServiceDemo.EventClass;
import com.ethereal.client.Core.Event.Annotation.AfterEvent;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import com.ethereal.client.Request.Annotation.RequestMethod;
import com.ethereal.client.Request.WebSocket.WebSocketRequest;
import com.ethereal.client.Service.Annotation.ServiceMethod;


public class ServerRequest extends WebSocketRequest {
    @RequestMethod
    public Boolean Register(String username, Long id){
        return false;
    }
    @RequestMethod
    public Boolean SendSay(Long listener_id, String message){
        return false;
    }


    @RequestMethod(invokeType = InvokeTypeFlags.Local)
    public Integer Add(Integer a, Integer b){
        return 23;
    }
    @RequestMethod(mapping = "test")
    @AfterEvent(function = "instance.after(ddd:d,s:sss)")
    public void test(String sss,Integer d){
        System.out.println("test");
    }
    @Override
    public void initialize() throws TrackException {
        name = "Client";
        types.add(Integer.class,"Int");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        types.add(User.class,"User");
        registerIoc("instance",new EventClass());
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }
}
