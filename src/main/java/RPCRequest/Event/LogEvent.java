package RPCRequest.Event;

import Model.RPCLog;
import RPCNet.Net;
import RPCRequest.Event.Delegate.OnLogDelegate;
import RPCRequest.Request;

import java.lang.ref.ReferenceQueue;
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
    public void onEvent(RPCLog log, Request request){
        synchronized (listeners){
            for (OnLogDelegate delegate:listeners) {
                delegate.OnLog(log,request);
            }
        }
    }
}
