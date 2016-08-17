package com.worldventures.dreamtrips.modules.picklocation.util;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.core.utils.LocationUtils;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class LocationSettingsDelegate {

   private static final int REQUEST_CHECK_SETTINGS = 4165;

   private PublishSubject<Boolean> locationSettingsStateObservable = PublishSubject.create();

   private final WeakReference<Activity> weakActivity;

   public LocationSettingsDelegate(Activity activity) {
      this.weakActivity = new WeakReference<>(activity);
   }

   public Observable<Boolean> getLocationSettingsStateObservable() {
      return locationSettingsStateObservable;
   }

   public void startLocationSettingsResolution(Status status) {
      if (!checkActivity()) return;
      try {
         status.startResolutionForResult(weakActivity.get(), REQUEST_CHECK_SETTINGS);
      } catch (IntentSender.SendIntentException e) {
         e.printStackTrace();
      }
   }

   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!checkActivity()) return false;
      switch (requestCode) {
         case REQUEST_CHECK_SETTINGS:
            switch (resultCode) {
               case Activity.RESULT_OK:
                  locationSettingsStateObservable.onNext(LocationUtils.isGpsOn(weakActivity.get()));
                  break;
               case Activity.RESULT_CANCELED:
                  // The user was asked to change settings, but chose not to
                  locationSettingsStateObservable.onNext(false);
                  break;
               default:
                  break;
            }
            return true;
      }
      return false;
   }

   private boolean checkActivity() {
      if (weakActivity.get() == null) {
         Timber.e("Location settings delegate activity was deallocated");
         return false;
      }
      return true;
   }
}
