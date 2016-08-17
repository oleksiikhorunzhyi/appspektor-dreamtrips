package com.worldventures.dreamtrips.core.rx.composer;

import rx.Observable;
import rx.schedulers.Schedulers;

public class DelayedIoComposer<T> extends DelayedComposer<T> {

   public DelayedIoComposer(long timeout) {
      super(timeout);
   }

   @Override
   public Observable<T> call(Observable<T> observable) {
      return super.call(observable).subscribeOn(Schedulers.io());
   }
}
