package ServiceDemo;

import com.ethereal.client.Core.Manager.Event.Annotation.Event;

import java.util.logging.ConsoleHandler;

public class EventClass {
    @Event(mapping = "after")
    public void Add(int ddd,String  s){
        System.out.println("After");
        System.out.println(ddd);
        System.out.println(s);
    }
}
