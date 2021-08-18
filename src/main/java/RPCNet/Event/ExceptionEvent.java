package RPCNet.Event;

import NativeClient.SocketClient;
import RPCNet.Event.Delegate.OnExceptionDelegate;
import RPCNet.Net;

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
    public void OnEvent(Exception exception, Net net){
        synchronized (listeners){
            for (OnExceptionDelegate delegate:listeners) {
                delegate.OnException(exception,net);
            }
        }
    }
}