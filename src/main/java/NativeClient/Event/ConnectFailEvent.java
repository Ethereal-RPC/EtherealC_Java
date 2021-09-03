package NativeClient.Event;

import NativeClient.Event.Delegate.OnConnectFailDelegate;
import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.SocketClient;

import java.util.Iterator;
import java.util.Vector;

public class ConnectFailEvent {
    Vector<OnConnectFailDelegate> listeners= new Vector<>();

    public void register(OnConnectFailDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(OnConnectFailDelegate delegate){
        synchronized (listeners){
            Iterator<OnConnectFailDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(SocketClient client) throws Exception {
        synchronized (listeners){
            for (OnConnectFailDelegate delegate:listeners) {
                delegate.OnConnectFail(client);
            }
        }
    }
}
