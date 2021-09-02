package RPCNet.Event;

import Model.RPCLog;
import NativeClient.SocketClient;
import RPCNet.Event.Delegate.OnLogDelegate;
import RPCNet.Net;

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
    public void onEvent(RPCLog log, Net net){
        synchronized (listeners){
            for (OnLogDelegate delegate:listeners) {
                delegate.OnLog(log,net);
            }
        }
    }
}
