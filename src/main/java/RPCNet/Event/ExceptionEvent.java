package RPCNet.Event;

import NativeClient.SocketClient;
import RPCNet.Event.Delegate.OnExceptionDelegate;
import RPCNet.Net;

import java.util.Vector;

public class ExceptionEvent {
    Vector<OnExceptionDelegate> listeners= new Vector<>();

    public void register(OnExceptionDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(OnExceptionDelegate delegate){
        synchronized (listeners){
            listeners.remove(delegate);
        }
    }
    public void onEvent(Exception exception, Net net){
        synchronized (listeners){
            for (OnExceptionDelegate delegate:listeners) {
                delegate.OnException(exception,net);
            }
        }
    }
}
