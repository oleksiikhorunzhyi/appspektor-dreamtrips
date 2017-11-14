package com.worldventures.wallet.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class LocationScreenComponent {

   public static final String COMPONENT_NAME = "com.worldventures.wallet.service.location.LocationSettingsService";

   private final PublishSubject<EnableResult> resultPublishSubject = PublishSubject.create();

   private static final int REQUEST_CODE = 0xFF01;
   private WeakReference<Activity> activityReference;

   public LocationScreenComponent(Activity activity) {
      this.activityReference = new WeakReference<>(activity);
   }

   public Observable<EnableResult> checkSettingsResult(LocationSettingsResult locationSettingsResult) {
      final Status status = locationSettingsResult.getStatus();
      switch (status.getStatusCode()) {
         case LocationSettingsStatusCodes.SUCCESS:
            notifyCallbacks(EnableResult.AVAILABLE);
            break;
         case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            try {
               Activity activity = activityReference.get();
               if (activity != null) {
                  status.startResolutionForResult(activity, REQUEST_CODE);
               }
            } catch (IntentSender.SendIntentException e) {
               Timber.e(e, "");
            }
            break;
      }
      return resultPublishSubject.asObservable().take(1);
   }

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

   public enum EnableResult {
      AVAILABLE, UNAVAILABLE
   }
}
