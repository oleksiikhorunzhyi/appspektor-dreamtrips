package com.worldventures.dreamtrips.modules.picklocation.util;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import com.worldventures.dreamtrips.modules.picklocation.LocationPicker;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class LocationResultHandler {

   private final WeakReference<Activity> weakActivity;

   public LocationResultHandler(Activity activity) {
      weakActivity = new WeakReference<Activity>(activity);
   }

   public void reportResultAndFinish(Location location, Throwable error) {
      if (weakActivity.get() == null) {
         Timber.e("Cannot report location result, activity was deallocated");
         return;
      }
      Intent intent = new Intent();
      if (location != null) {
         intent.putExtra(LocationPicker.LOCATION_EXTRA, location);
      }
      if (error != null) {
         intent.putExtra(LocationPicker.ERROR_EXTRA, error);
      }
      Activity activity = weakActivity.get();
      activity.setResult(Activity.RESULT_OK, intent);
      activity.finish();
   }
}
