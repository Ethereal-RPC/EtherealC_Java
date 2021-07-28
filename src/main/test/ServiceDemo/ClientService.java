package ServiceDemo;

import Annotation.RPCService;
import Model.User;

public class ClientService {
    @RPCService
    public void Say(User sender, String message)
    {
        System.out.println(sender.getUsername() + ":" + message);
    }
}
