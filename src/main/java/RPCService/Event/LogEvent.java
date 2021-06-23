package RPCService.Event;

import Model.RPCLog;
import RPCRequest.Request;
import RPCService.Event.Delegate.OnLogDelegate;
import RPCService.Service;

import java.util.Vector;

public class LogEvent {
    Vector<OnLogDelegate> listeners= new Vector<>();

    public void register(OnLogDelegate delegate){
        synchronized (listeners){
            listeners.add(delegate);
        }
    }
    public void unRegister(OnLogDelegate delegate){
        synchronized (listeners){
            listeners.remove(delegate);
        }
    }
    public void OnEvent(RPCLog log, Service service){
        synchronized (listeners){
            for (OnLogDelegate delegate:listeners) {
                delegate.OnLog(log,service);
            }
        }
    }
}
