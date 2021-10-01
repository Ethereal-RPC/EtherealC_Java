package com.ethereal.client.Core.Event;

import com.ethereal.client.Core.Event.Delegate.LogEventDelegate;
import com.ethereal.client.Core.Model.TrackLog;

import java.util.Iterator;
import java.util.Vector;

public class LogEvent {
    Vector<LogEventDelegate> listeners= new Vector<>();

    public void register(LogEventDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(LogEventDelegate delegate){
        synchronized (listeners){
            Iterator<LogEventDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(TrackLog log){
        synchronized (listeners){
            for (LogEventDelegate delegate:listeners) {
                delegate.onLog(log);
            }
        }
    }
}
