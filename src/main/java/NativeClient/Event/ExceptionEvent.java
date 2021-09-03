package NativeClient.Event;

import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.Event.Delegate.OnExceptionDelegate;
import NativeClient.SocketClient;

import java.util.Iterator;
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
            Iterator<OnExceptionDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
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
