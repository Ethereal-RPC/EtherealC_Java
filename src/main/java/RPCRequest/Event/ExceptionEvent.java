package RPCRequest.Event;

import RPCNet.Net;
import RPCRequest.Event.Delegate.OnExceptionDelegate;
import RPCRequest.Request;

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
    public void OnEvent(Exception exception, Request request) throws Exception {
        synchronized (listeners){
            for (OnExceptionDelegate delegate:listeners) {
                delegate.OnException(exception,request);
            }
        }
    }
}
