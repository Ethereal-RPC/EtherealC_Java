package NativeClient.Event;

import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.Event.Delegate.OnExceptionDelegate;
import NativeClient.SocketClient;

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
            listeners.remove(delegate);
        }
    }
    public void OnEvent(SocketClient client)  {
        synchronized (listeners){
            for (OnConnectSuccessDelegate delegate:listeners) {
                delegate.OnConnectSuccess(client);
            }
        }
    }
}
