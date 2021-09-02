package NativeClient.Event;

import NativeClient.Event.Delegate.OnExceptionDelegate;
import NativeClient.SocketClient;

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
    public void onEvent(Exception exception, SocketClient client) throws Exception {
        synchronized (listeners){
            for (OnExceptionDelegate delegate:listeners) {
                delegate.OnException(exception,client);
            }
        }
    }
}
