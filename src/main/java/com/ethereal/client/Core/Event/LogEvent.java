package com.ethereal.client.Core.Event;

import com.ethereal.client.Core.Event.Delegate.LogEventDelegte;
import com.ethereal.client.Core.Model.TrackLog;

import java.util.Iterator;
import java.util.Vector;

public class LogEvent {
    Vector<LogEventDelegte> listeners= new Vector<>();

    public void register(LogEventDelegte delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(LogEventDelegte delegate){
        synchronized (listeners){
            Iterator<LogEventDelegte> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(TrackLog log){
        synchronized (listeners){
            for (LogEventDelegte delegate:listeners) {
                delegate.onLog(log);
            }
        }
    }
}
