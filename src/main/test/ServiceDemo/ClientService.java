package ServiceDemo;

import Service.Annotation.Service;
import Core.Model.User;

public class ClientService {
    @Service
    public void Say(User sender, String message)
    {
        System.out.println(sender.getUsername() + ":" + message);
    }
}
