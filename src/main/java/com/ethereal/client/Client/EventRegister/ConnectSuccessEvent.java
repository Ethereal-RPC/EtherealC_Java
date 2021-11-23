package com.ethereal.client.Client.EventRegister;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.EventRegister.Delegate.OnConnectSuccessDelegate;

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
    public void onEvent(Client client)  {
        synchronized (listeners){
            for (OnConnectSuccessDelegate item : listeners){
                item.OnConnectSuccess(client);
            }
        }
    }
}
