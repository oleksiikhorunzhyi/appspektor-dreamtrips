package com.worldventures.dreamtrips.core.location;

import android.app.Activity;
import android.content.IntentSender;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import rx.Observable;
import timber.log.Timber;

public class LocationServiceDispatcher {

   private Activity activity;
   private LocationManager locationManager;
   private GoogleApiClient googleApiClient;

   public LocationServiceDispatcher(Activity activity, LocationManager locationManager, GoogleApiClient googleApiClient) {
      this.activity = activity;
      this.locationManager = locationManager;
      this.googleApiClient = googleApiClient;
   }

   public Observable<Boolean> checkEnableLocationService() {
      return Observable.just(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
   }

   public void requestLocationSettings() {
      LocationRequest locationRequest = LocationRequest.create();
      locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      locationRequest.setInterval(30 * 1000);
      locationRequest.setFastestInterval(5 * 1000);
      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
      builder.setAlwaysShow(true);

      PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
      pendingResult.setResultCallback(result -> {
         final Status status = result.getStatus();
         switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
               try {
                  status.startResolutionForResult(activity, 1000);
               } catch (IntentSender.SendIntentException e) {
                  Timber.e(e, "");
               }
               break;
         }
      });
   }
}
