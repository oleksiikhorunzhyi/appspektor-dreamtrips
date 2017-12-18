package com.worldventures.dreamtrips.modules.common.delegate;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Base class for usage with event bus like logic.
 * Instances can be provided through DI as singleton.
 */
public class EventDelegate<T> {

   protected final Subject<T, T> publishSubject = PublishSubject.<T>create().toSerialized();

   public void post(T event) {
      publishSubject.onNext(event);
   }

   public Observable<T> getObservable() {
      return publishSubject;
   }
}
