package ServiceDemo;

import Model.User;
import com.ethereal.client.Service.Annotation.ServiceMethod;
import com.ethereal.client.Service.WebSocket.WebSocketService;

public class ClientService extends WebSocketService {

    @ServiceMethod
    public void Say(User sender, String message)
    {
        System.out.println(sender.getUsername() + ":" + message);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void unInitialize() {

    }
}
