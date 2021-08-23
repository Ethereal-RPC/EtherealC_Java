package NativeClient.Event;

import Model.RPCLog;
import NativeClient.Event.Delegate.OnLogDelegate;
import NativeClient.SocketClient;

import java.util.Vector;

public class LogEvent {
    Vector<OnLogDelegate> listeners= new Vector<>();

    public void register(OnLogDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(OnLogDelegate delegate){
        synchronized (listeners){
            listeners.remove(delegate);
        }
    }
    public void OnEvent(RPCLog log, SocketClient client){
        synchronized (listeners){
            for (OnLogDelegate delegate:listeners) {
                delegate.OnLog(log,client);
            }
        }
    }
}
