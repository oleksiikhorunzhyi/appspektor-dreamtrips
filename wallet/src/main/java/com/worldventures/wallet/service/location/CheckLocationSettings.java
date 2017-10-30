package com.worldventures.wallet.service.location;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import rx.Observable;
import rx.Subscriber;

final class CheckLocationSettings extends GoogleApiObservable<LocationSettingsResult> {

   private final LocationRequest locationRequest;

   private CheckLocationSettings(Context context, LocationRequest locationRequest) {
      super(new GoogleApiClient.Builder(context).addApi(LocationServices.API));
      this.locationRequest = locationRequest;
   }

   static Observable<LocationSettingsResult> createObservable(Context context, LocationRequest locationRequest) {
      return Observable.create(new CheckLocationSettings(context, locationRequest));
   }

   static LocationSettingsResult checkSettings(GoogleApiClient googleApiClient, LocationRequest locationRequest) {
      return LocationServices.SettingsApi.checkLocationSettings(googleApiClient, new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build())
            .await();
   }

   @Override
   protected void execute(GoogleApiClient googleApiClient, Subscriber<? super LocationSettingsResult> subscriber) {
      subscriber.onNext(checkSettings(googleApiClient, locationRequest));
      subscriber.onCompleted();
   }
}
