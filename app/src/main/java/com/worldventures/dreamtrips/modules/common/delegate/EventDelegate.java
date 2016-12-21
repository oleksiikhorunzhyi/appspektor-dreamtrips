package com.worldventures.dreamtrips.modules.common.delegate;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Base class for usage with event bus like logic.
 * Instances can be provided through DI as singleton.
 */
public class EventDelegate<Event> {

   protected final Subject<Event, Event> publishSubject = PublishSubject.<Event>create().toSerialized();

   public void post(Event event) {
      publishSubject.onNext(event);
   }

   public Observable<Event> getObservable() {
      return publishSubject;
   }
}
