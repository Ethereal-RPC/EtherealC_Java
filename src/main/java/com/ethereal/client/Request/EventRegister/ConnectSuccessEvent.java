package com.ethereal.client.Request.EventRegister;

import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.EventRegister.Delegate.OnConnectSuccessDelegate;

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
    public void onEvent(Request request){
        synchronized (listeners){
            for (OnConnectSuccessDelegate delegate:listeners) {
                delegate.OnConnectSuccess(request);
            }
        }
    }
}
