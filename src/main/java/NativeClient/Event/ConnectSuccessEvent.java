package NativeClient.Event;

import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.Event.Delegate.OnExceptionDelegate;
import NativeClient.SocketClient;

import java.util.Iterator;
import java.util.Vector;

public class ConnectSuccessEvent {
    Vector<OnConnectSuccessDelegate> listeners= new Vector<>();

    public void register(OnConnectSuccessDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(OnConnectSuccessDelegate delegate){
        synchronized (listeners){
            Iterator<OnConnectSuccessDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(SocketClient client)  {
        synchronized (listeners){
            for (OnConnectSuccessDelegate item : listeners){
                item.OnConnectSuccess(client);
            }
        }
    }
}
