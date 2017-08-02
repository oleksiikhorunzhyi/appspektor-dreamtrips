package com.worldventures.dreamtrips.wallet.service.location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

abstract class GoogleApiObservable<T> implements Observable.OnSubscribe<T> {

   final GoogleApiClient client;

   GoogleApiObservable(GoogleApiClient.Builder apiBuilder) {
      client = apiBuilder.build();
   }

   @Override
   public final void call(Subscriber<? super T> subscriber) {
      final ConnectionResult connectionResult = client.blockingConnect();
      final GoogleApiClient googleApiClient = client;
      if (connectionResult.isSuccess()) {
         Timber.d("GoogleApiClient %s connected", googleApiClient);
         execute(googleApiClient, subscriber);
      } else {
         subscriber.onError(new RuntimeException("GoogleApiClient is disconnected"));
      }
      subscriber.add(Subscriptions.create(() -> {
         if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Timber.d("GoogleApiClient %s disconnected", googleApiClient);
         }
      }));
   }

   protected void unsubscribe(GoogleApiClient client) {
      //do nothing here
   }

   protected abstract void execute(GoogleApiClient googleApiClient, Subscriber<? super T> subscriber);

}
