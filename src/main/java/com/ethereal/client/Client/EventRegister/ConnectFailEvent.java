package com.ethereal.client.Client.EventRegister;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.EventRegister.Delegate.OnConnectFailDelegate;

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
    public void onEvent(Client client)  {
        synchronized (listeners){
            for (OnConnectFailDelegate item : listeners){
                item.OnConnectFail(client);
            }
        }
    }
}
