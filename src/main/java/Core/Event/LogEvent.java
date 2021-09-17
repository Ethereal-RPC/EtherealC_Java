package Core.Event;

import Core.Event.Delegate.LogEventDelegte;
import Core.Model.RPCLog;

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
    public void onEvent(RPCLog log){
        synchronized (listeners){
            for (LogEventDelegte delegate:listeners) {
                delegate.onLog(log);
            }
        }
    }
}
