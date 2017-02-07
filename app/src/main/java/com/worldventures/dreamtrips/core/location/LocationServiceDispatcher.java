package com.worldventures.dreamtrips.core.location;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import rx.Observable;

public class LocationServiceDispatcher {

   private LocationManager locationManager;
   private Activity activity;

   public LocationServiceDispatcher(LocationManager locationManager, Activity activity) {
      this.locationManager = locationManager;
      this.activity = activity;
   }

   public Observable<Boolean> requestEnableLocationService() {
      if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
         return Observable.just(true);
      } else {
         return Observable.just(false)
               .doOnSubscribe(() -> openSettings());
      }
   }

   private void openSettings() {
      Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      activity.startActivity(myIntent);
   }

   public Observable<Boolean> isGPSEnabled() {
      return Observable.just(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
   }
}
