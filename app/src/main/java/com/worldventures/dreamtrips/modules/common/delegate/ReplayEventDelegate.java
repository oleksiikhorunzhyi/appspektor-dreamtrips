package com.worldventures.dreamtrips.modules.common.delegate;

import rx.Observable;

public class ReplayEventDelegate<Event> extends EventDelegate<Event> {

   private Event lastEvent;
   // use separate flag to indicate cache was reset as correct event value can be null as well in descendants
   private boolean cacheIsEmpty = true;

   public ReplayEventDelegate(ReplayEventDelegatesWiper wiper) {
      wiper.register(this);
   }

   @Override
   public void post(Event event) {
      super.post(event);
      lastEvent = event;
      cacheIsEmpty = false;
   }

   public Observable<Event> getReplayObservable() {
      return Observable.merge(Observable.just(lastEvent).filter(event -> !cacheIsEmpty),
            publishSubject);
   }

   public void clearReplays() {
      lastEvent = null;
      cacheIsEmpty = true;
   }
}
