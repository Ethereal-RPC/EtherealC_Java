package ServiceDemo;

import Model.User;
import com.ethereal.client.Core.Event.Annotation.AfterEvent;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Service.Annotation.ServiceMethod;
import com.ethereal.client.Service.WebSocket.WebSocketService;


public class ClientService extends WebSocketService {
    public ClientService(){

    }

    @ServiceMethod(mapping = "Say")
    public void Say(User sender, String message)
    {
        System.out.println(sender.getUsername() + ":" + message);
    }
    @ServiceMethod(mapping = "test")
    @AfterEvent(function = "instance.after(ddd:d,s:sss)")
    public void test(String sss,Integer d){
        System.out.println("test");
    }


    @Override
    public void initialize() throws TrackException {
        name = "Server";
        types.add(Integer.class,"Int");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        types.add(User.class,"User");
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }
}
