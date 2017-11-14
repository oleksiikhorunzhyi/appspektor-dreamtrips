package com.worldventures.core.service.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import icepick.Icepick;
import timber.log.Timber;

public abstract class Tracker {

   private HashMap<String, Object> headerData;

   public abstract String getKey();

   public abstract void onCreate(@Nullable Activity activity);

   public void onStart(@Nullable Activity activity) {
      //do nothing
   }

   public void onStop(@Nullable Activity activity) {
      //do nothing
   }

   public void onResume(@Nullable Activity activity) {
      //do nothing
   }

   public void onPause(@Nullable Activity activity) {
      //do nothing
   }

   public void setHeaderData(HashMap<String, Object> data) {
      headerData = data;
   }

   public void onSaveInstanceState(Bundle outState) {
      Icepick.saveInstanceState(this, outState);
   }

   public void onRestoreInstanceState(Bundle savedInstanceState) {
      Icepick.restoreInstanceState(this, savedInstanceState);
   }

   public abstract void trackEvent(String category, String action, Map<String, Object> data);

   /**
    * Performs null check and warns log if activity is null.
    *
    * @param activity instance to check
    * @return true if activity is null
    */
   protected boolean checkNullAndWarn(@Nullable Activity activity) {
      if (activity == null) {
         Timber.e(this.getClass().getName() + " lifecycle method: activity got weak");
      }
      return activity == null;
   }

   public HashMap<String, Object> getHeaderData() {
      return headerData;
   }
}
