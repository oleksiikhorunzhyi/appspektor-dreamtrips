package com.worldventures.dreamtrips.core.rx.composer;

import rx.Subscriber;
import timber.log.Timber;

public class StubSubscriber<T> extends Subscriber<T> {
   @Override
   public void onCompleted() {

   }

   @Override
   public void onError(Throwable e) {
      Timber.w(e, "Error stubbed at subscriber");
   }

   @Override
   public void onNext(T t) {

   }
}
