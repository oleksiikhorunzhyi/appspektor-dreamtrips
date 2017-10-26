package com.worldventures.wallet.service.location;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.worldventures.wallet.util.LocationUnavailableException;

import rx.Subscriber;
import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static com.worldventures.wallet.service.location.CheckLocationSettings.checkSettings;

class LastKnownLocationObservable extends GoogleApiObservable<Location> {

   private static final int INTERVAL = 4500;
   private LocationListener listener;

   LastKnownLocationObservable(Context context) {
      super(new GoogleApiClient.Builder(context).addApi(LocationServices.API));
   }

   @Override
   protected void execute(GoogleApiClient googleApiClient, Subscriber<? super Location> subscriber) {
      Location location = FusedLocationApi.getLastLocation(googleApiClient);
      if (location != null) {
         subscriber.onNext(location);
         subscriber.onCompleted();
         return;
      }
      requestLocationUpdate(googleApiClient, subscriber);
   }

   private void requestLocationUpdate(GoogleApiClient googleApiClient, Subscriber<? super Location> subscriber) {
      final LocationSettingsResult locationSettingsResult = checkSettings(googleApiClient, provideLocationRequest());
      final Status status = locationSettingsResult.getStatus();
      if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
         subscriber.onError(new LocationUnavailableException(locationSettingsResult));
         return;
      }

      listener = location -> {
         subscriber.onNext(location);
         subscriber.onCompleted();
         Timber.d("Location is updated %s", location);
         Looper looper = Looper.myLooper();
         if (looper != null) looper.quit();
      };
      Looper.prepare();
      FusedLocationApi.requestLocationUpdates(googleApiClient, provideLocationRequest(), listener, Looper.myLooper());
      Timber.d("Request location updates with %s", googleApiClient);
   }

   static LocationRequest provideLocationRequest() {
      return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setFastestInterval(INTERVAL)
            .setInterval(INTERVAL);
   }

   @Override
   protected void unsubscribe(GoogleApiClient client) {
      if (listener != null) {
         FusedLocationApi.removeLocationUpdates(client, listener);
         Timber.d("Remove location updates with %s", client);
      }
      super.unsubscribe(client);
   }
}
