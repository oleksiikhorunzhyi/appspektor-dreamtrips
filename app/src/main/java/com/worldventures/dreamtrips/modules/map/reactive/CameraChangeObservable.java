package com.worldventures.dreamtrips.modules.map.reactive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

public class CameraChangeObservable implements Observable.OnSubscribe<CameraPosition> {
   private final GoogleMap map;

   public static Observable<CameraPosition> create(GoogleMap map) {
      return Observable.create(new CameraChangeObservable(map));
   }

   public CameraChangeObservable(GoogleMap map) {
      this.map = map;
   }

   @Override
   public void call(final Subscriber<? super CameraPosition> subscriber) {
      verifyMainThread();

      map.setOnCameraIdleListener(() -> {
         if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(map.getCameraPosition());
         }
      });

      subscriber.add(new MainThreadSubscription() {
         @Override
         protected void onUnsubscribe() {
            map.setOnCameraIdleListener(null);
         }
      });
   }
}
