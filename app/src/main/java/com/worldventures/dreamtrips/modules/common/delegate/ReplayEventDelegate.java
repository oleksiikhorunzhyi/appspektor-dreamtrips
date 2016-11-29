package com.worldventures.dreamtrips.modules.common.delegate;

import rx.Observable;

/**
 * Base class for usage with event bus like logic.
 * Instances can be provided through DI as singleton.
 */
public class ReplayEventDelegate<Event> extends EventDelegate<Event> {

   private Event lastEvent;
   // use separate flag to indicate cache was reset as correct event value can be null as well in descendants
   private boolean cacheIsEmpty;

   public Observable<Event> getReplayObservable() {
      return Observable.merge(Observable.just(lastEvent).filter(event -> !cacheIsEmpty),
            publishSubject);
   }

   public void clearReplays() {
      lastEvent = null;
      cacheIsEmpty = true;
   }
}
