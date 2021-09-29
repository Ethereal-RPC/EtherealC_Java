package com.ethereal.client.Client.Event;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.Event.Delegate.OnConnectDelegate;

import java.util.Iterator;
import java.util.Vector;

public class ConnectEvent {
    Vector<OnConnectDelegate> listeners= new Vector<>();

    public void register(OnConnectDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(OnConnectDelegate delegate){
        synchronized (listeners){
            Iterator<OnConnectDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(Client client)  {
        synchronized (listeners){
            for (OnConnectDelegate item : listeners){
                item.OnConnectSuccess(client);
            }
        }
    }
}
