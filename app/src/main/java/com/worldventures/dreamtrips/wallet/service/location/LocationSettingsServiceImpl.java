package com.worldventures.dreamtrips.wallet.service.location;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class LocationSettingsServiceImpl extends LocationSettingsService {

   private static final int REQUEST_CODE = 0xFF01;
   private Activity activity;
   private GoogleApiClient googleApiClient;

   public LocationSettingsServiceImpl(Activity activity, GoogleApiClient googleApiClient) {
      this.activity = activity;
      this.googleApiClient = googleApiClient;
   }

   private void checkLocationApi() {
      // todo remove this request
      LocationRequest locationRequest = LocationRequest.create();
      locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      locationRequest.setInterval(30 * 1000);
      locationRequest.setFastestInterval(5 * 1000);
      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
      builder.setAlwaysShow(true);

      PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi
            .checkLocationSettings(googleApiClient, builder.build());
      pendingResult.setResultCallback(result -> {
         final Status status = result.getStatus();
         switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
               notifyCallbacks(EnableResult.AVAILABLE);
               return;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
               try {
                  status.startResolutionForResult(activity, REQUEST_CODE);
               } catch (IntentSender.SendIntentException e) {
                  Timber.e(e, "");
               }
               break;
         }
      });
   }

   @Override
   public Observable<EnableResult> enableLocationApi() {
      checkLocationApi();
      return resultPublishSubject.asObservable();
   }

   private final PublishSubject<EnableResult> resultPublishSubject = PublishSubject.create();

   private void notifyCallbacks(EnableResult enableResult) {
      resultPublishSubject.onNext(enableResult);
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_CODE) {
         notifyCallbacks(resultCode == Activity.RESULT_OK ? EnableResult.AVAILABLE : EnableResult.UNAVAILABLE);
         return true;
      }
      return false;
   }
}
