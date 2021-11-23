package RequestDemo;

import Model.User;
import ServiceDemo.EventClass;
import com.ethereal.client.Core.Manager.Event.Annotation.AfterEvent;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import com.ethereal.client.Request.Annotation.RequestMapping;
import com.ethereal.client.Request.WebSocket.WebSocketRequest;


public class ServerRequest extends WebSocketRequest {
    @RequestMapping(mapping = "Register")
    public Boolean Register(String username, Long id){
        return false;
    }
    @RequestMapping(mapping = "SendSay")
    public Boolean SendSay(Long listener_id, String message){
        return false;
    }
    @RequestMapping(mapping = "Add",invokeType = InvokeTypeFlags.Local)
    public Integer Add(Integer a, Integer b){
        return 23;
    }
    @RequestMapping(mapping = "test")
    @AfterEvent(function = "instance.after(ddd:d,s:s)")
    public Boolean test(String s,Integer d,Integer k){
        System.out.println("test");
        return false;
    }
    @Override
    public void initialize() throws TrackException {
        name = "Server";
        types.add(Integer.class,"Int");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        types.add(User.class,"User");
        iocManager.register("instance",new EventClass());
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }

    @Override
    public void unInitialize() {

    }
}
