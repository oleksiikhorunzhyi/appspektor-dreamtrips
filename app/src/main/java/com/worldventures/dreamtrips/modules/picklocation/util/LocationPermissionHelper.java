package com.worldventures.dreamtrips.modules.picklocation.util;

import com.worldventures.dreamtrips.modules.picklocation.view.PickLocationActivity;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class LocationPermissionHelper {

   private WeakReference<PickLocationActivity> weakActivity;

   public LocationPermissionHelper(PickLocationActivity pickLocationActivity) {
      this.weakActivity = new WeakReference<PickLocationActivity>(pickLocationActivity);
   }

   public void askForLocationPermission() {
      if (weakActivity.get() == null) {
         Timber.e("Cannot ask for location permissions, activity was deallocated");
         return;
      }
      weakActivity.get().askForLocationPermission();
   }
}
