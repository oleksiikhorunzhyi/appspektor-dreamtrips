package com.worldventures.core.service.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.worldventures.janet.analytics.AnalyticsEvent;

import java.lang.ref.WeakReference;

@AnalyticsEvent(
      category = LifecycleEvent.LIFECYCLE_CATEGORY,
      trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class LifecycleEvent extends BaseAnalyticsAction {

   public static final String LIFECYCLE_CATEGORY = "lifecycle";
   public static final String ACTION_ONCREATE = "create";
   public static final String ACTION_ONSTART = "start";
   public static final String ACTION_ONRESUME = "resume";
   public static final String ACTION_ONPAUSE = "pause";
   public static final String ACTION_ONSTOP = "stop";
   public static final String ACTION_ONSAVESTATE = "save_state";
   public static final String ACTION_ONRESTORESTATE = "restore_state";

   private final String action;
   private Bundle state;
   private WeakReference<Activity> weakActivity;

   public LifecycleEvent(Activity activity, String action) {
      this.action = action;
      weakActivity = new WeakReference<Activity>(activity);
   }

   public LifecycleEvent(String action, Bundle state) {
      this.action = action;
      this.state = state;
   }

   public String getAction() {
      return action;
   }

   public Bundle getState() {
      return state;
   }

   @Nullable
   public Activity getActivity() {
      return weakActivity.get();
   }
}
