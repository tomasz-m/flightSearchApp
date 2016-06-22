package info.tomaszminiach.flightsapptest.helper;

import java.lang.ref.WeakReference;

public  class SimpleSingleObserver {

    private static WeakReference<SimpleObserver> theSimpleObserver_weak;
//    private static SimpleObserver theSingleObserver;

    public static void subscribe(SimpleObserver theSingleObserver) {
 //       SimpleSingleObserver.theSingleObserver = theSingleObserver;
        theSimpleObserver_weak = new WeakReference<SimpleObserver>(theSingleObserver);
    }
    public static void unsubscribe() {
 //       SimpleSingleObserver.theSingleObserver = null;
    }

    public static void sendEvent(String eventTag, Object object){
//        if(theSingleObserver!=null)
//            theSingleObserver.onEvent(object);
        if(theSimpleObserver_weak.get()!=null)
            theSimpleObserver_weak.get().onEvent(eventTag, object);
    }

    public interface SimpleObserver{
        void onEvent(String eventTag, Object o);
    }

}