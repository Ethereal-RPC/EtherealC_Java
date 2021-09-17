package Core.Event;

import Core.Event.Delegate.ExceptionEventDelegate;
import Core.Model.TrackException;

import java.util.Iterator;
import java.util.Vector;

public class ExceptionEvent {
    Vector<ExceptionEventDelegate> listeners= new Vector<>();

    public void register(ExceptionEventDelegate delegate){
        synchronized (listeners){
            if(!listeners.contains(delegate)) listeners.add(delegate);
        }
    }
    public void unRegister(ExceptionEventDelegate delegate){
        synchronized (listeners){
            Iterator<ExceptionEventDelegate> iterator = listeners.iterator();
            while(iterator.hasNext() && iterator.next() == delegate){
                iterator.remove();
            }
        }
    }
    public void onEvent(TrackException exception)  {
        synchronized (listeners){
            for (ExceptionEventDelegate delegate:listeners) {
                delegate.onException(exception);
            }
        }
    }
}