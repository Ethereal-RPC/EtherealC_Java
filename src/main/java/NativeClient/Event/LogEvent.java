package NativeClient.Event;

import Model.RPCLog;
import NativeClient.Event.Delegate.OnExceptionDelegate;
import NativeClient.Event.Delegate.OnLogDelegate;
import NativeClient.SocketClient;

import java.util.Iterator;
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
            Iterator<OnLogDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(RPCLog log, SocketClient client){
        synchronized (listeners){
            for (OnLogDelegate delegate:listeners) {
                delegate.OnLog(log,client);
            }
        }
    }
}
