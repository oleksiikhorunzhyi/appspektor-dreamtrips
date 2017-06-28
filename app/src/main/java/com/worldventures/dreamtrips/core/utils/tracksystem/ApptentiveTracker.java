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
      if (!BuildConfig.QA_AUTOMATION_MODE_ENABLED) Apptentive.register(application, BuildConfig.SURVEY_API_TOKEN);
   }

   @Override
   public String getKey() {
      return TRACKER_KEY;
   }

   @Override
   public void onCreate(@Nullable Activity activity) {
      boolean trackerEnabled = !BuildConfig.QA_AUTOMATION_MODE_ENABLED;
      Timber.v("enabled: " + trackerEnabled);
   }

   public void onStart(@Nullable Activity activity) {
      if (BuildConfig.QA_AUTOMATION_MODE_ENABLED || checkNullAndWarn(activity)) return;
      this.activity = new WeakReference<>(activity);
   }

   public void onStop(@Nullable Activity activity) {
      if (BuildConfig.QA_AUTOMATION_MODE_ENABLED || checkNullAndWarn(activity)) return;
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
      if (BuildConfig.QA_AUTOMATION_MODE_ENABLED || activity == null) return;

      Activity activity = this.activity.get();
      if (activity != null) {
         Apptentive.engage(activity, TextUtils.join(":", new String[]{category, action}));
      }
   }
}
