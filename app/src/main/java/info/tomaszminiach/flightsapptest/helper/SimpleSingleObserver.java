package info.tomaszminiach.flightsapptest.helper;

import java.lang.ref.WeakReference;

/**
 * I've decided to use a simple custom 'event bus'.
 * In such cases libraries like otto can be used but this app is not complicated and
 * I wanted to reduce external dependencies to make it clear and eliminate any possible problems with building on other machines
 * It allows only one observer
 */
public  class SimpleSingleObserver {

    //weak reference to avoid leaks
    private static WeakReference<SimpleObserver> theSimpleObserver_weak;

    public static void subscribe(SimpleObserver theSingleObserver) {
        theSimpleObserver_weak = new WeakReference<SimpleObserver>(theSingleObserver);
    }

    public static void unsubscribe() {
        //probably no need to call that - its weak reference anyway
 //     theSimpleObserver_weak = null;
    }

    public static void sendEvent(String eventTag, Object object){
        if(theSimpleObserver_weak.get()!=null)
            theSimpleObserver_weak.get().onEvent(eventTag, object);
    }

    public interface SimpleObserver{
        void onEvent(String eventTag, Object o);
    }

}