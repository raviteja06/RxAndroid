package demo.rxandroid.rxbus_fragments;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Raviteja on 9/25/2016. RxAndroid
 */

public class RxBus {
    private static RxBus instance;

    /**
     * Replace any specific object in place of object to handle object based bus
     */
    private PublishSubject<Object> subject = PublishSubject.create();

    public static RxBus instanceOf() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }

    /**
     * Pass any event down to event listeners.
     */
    public void send(Object object) {
        subject.onNext(object);
    }

    /**
     * Subscribe to this Observable. On event, do something
     * e.g. replace a fragment
     */
    public Observable<Object> getEvents() {
        return subject;
    }
}
