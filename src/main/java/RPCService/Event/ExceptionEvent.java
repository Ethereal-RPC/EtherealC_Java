package RPCService.Event;

import RPCRequest.Request;
import RPCService.Event.Delegate.OnExceptionDelegate;
import RPCService.Service;

import java.util.Vector;

public class ExceptionEvent {
    Vector<OnExceptionDelegate> listeners= new Vector<>();

    public void register(OnExceptionDelegate delegate){
        synchronized (listeners){
            listeners.add(delegate);
        }
    }
    public void unRegister(OnExceptionDelegate delegate){
        synchronized (listeners){
            listeners.remove(delegate);
        }
    }
    public void OnEvent(Exception exception, Service service){
        synchronized (listeners){
            for (OnExceptionDelegate delegate:listeners) {
                delegate.OnException(exception,service);
            }
        }
    }
}
