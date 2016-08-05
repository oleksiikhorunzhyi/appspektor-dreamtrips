package com.worldventures.dreamtrips.modules.common.delegate;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Base class for usage with event bus like logic.
 * Instances can be provided through DI as singleton.
 */
public class EventDelegate<Event> {

    private PublishSubject<Event> publishSubject = PublishSubject.create();

    public void post(Event event) {
        publishSubject.onNext(event);
    }

    public Observable<Event> getObservable() {
        return publishSubject;
    }
}
