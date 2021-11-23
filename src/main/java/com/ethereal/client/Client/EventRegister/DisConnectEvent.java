package com.ethereal.client.Client.EventRegister;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.EventRegister.Delegate.OnDisConnectDelegate;

import java.util.Iterator;
import java.util.Vector;

public class DisConnectEvent {
    Vector<OnDisConnectDelegate> listeners= new Vector<>();

    public void register(OnDisConnectDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(OnDisConnectDelegate delegate){
        synchronized (listeners){
            Iterator<OnDisConnectDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(Client client)  {
        synchronized (listeners){
            for (OnDisConnectDelegate delegate:listeners) {
                delegate.OnDisConnect(client);
            }
        }
    }
}
