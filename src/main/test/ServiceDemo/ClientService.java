package ServiceDemo;

import Model.User;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Service.Annotation.Service;
import com.ethereal.client.Service.WebSocket.WebSocketService;

public class ClientService extends WebSocketService {

    @Service
    public void Say(User sender, String message)
    {
        System.out.println(sender.getUsername() + ":" + message);
    }

}
