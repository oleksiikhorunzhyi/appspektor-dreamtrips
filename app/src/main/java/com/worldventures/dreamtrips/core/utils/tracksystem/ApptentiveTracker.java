package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.apptentive.android.sdk.Apptentive;
import com.worldventures.dreamtrips.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.Map;

import timber.log.Timber;

public class ApptentiveTracker extends Tracker {

   public static final String TRACKER_KEY = "apptentive_tracker";

   private WeakReference<Activity> activity;

   public ApptentiveTracker(Application application) {
      Apptentive.register(application, BuildConfig.SURVEY_API_TOKEN);
   }

   @Override
   public String getKey() {
      return TRACKER_KEY;
   }

   @Override
   public void onCreate(@Nullable Activity activity) {
      Timber.v("onCreate");
   }

   public void onStart(@Nullable Activity activity) {
      Timber.v("onStart");
      this.activity = new WeakReference<>(activity);
   }

   public void onStop(@Nullable Activity activity) {
      Timber.v("onStop");
   }

   @Override
   public void onResume(@Nullable Activity activity) {
      Timber.v("onResume");
   }

   @Override
   public void onPause(@Nullable Activity activity) {
      Timber.v("onPause");
   }

   @Override
   public void trackEvent(String category, String action, Map<String, Object> data) {
      if (activity == null) return;

      Activity activity = this.activity.get();
      if (activity != null) {
         Apptentive.engage(activity, TextUtils.join(":", new String[]{category, action}));
      }
   }
}
