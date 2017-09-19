package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Deprecated - use {@link AnalyticsInteractor}
 */
@Deprecated
public class TrackingHelper {

   private static final String KEY_ADOBE_TRACKER = "adobe_tracker";

   public static final String TYPE = "type";
   public static final String ID = "id";

   private static Map<String, Tracker> trackers = new HashMap<>();

   public static void init(Collection<Tracker> trackerSet) {
      Queryable.from(trackerSet).forEachR(tracker -> trackers.put(tracker.getKey(), tracker));
   }

   private TrackingHelper() {
   }

   public static void onCreate(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onCreate(activity);
      }
   }

   public static void onStart(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onStart(activity);
      }
   }

   public static void onStop(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onStop(activity);
      }
   }

   public static void onResume(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onResume(activity);
      }
   }

   public static void onPause(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onPause(activity);
      }
   }

   public static void onSaveInstanceState(Bundle outState) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onSaveInstanceState(outState);
      }
   }

   public static void onRestoreInstanceState(Bundle savedInstanceState) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onRestoreInstanceState(savedInstanceState);
      }
   }

   // ---------------- Tracking helper methods

   public static void setUserId(String username, String userId) {
      HashMap<String, Object> headerData = new HashMap<>();
      headerData.put("member_id", username);
      headerData.put("old_member_id", userId);
      trackers.get(KEY_ADOBE_TRACKER).setHeaderData(headerData);
   }

   public static void clearHeaderData() {
      trackers.get(KEY_ADOBE_TRACKER).setHeaderData(null);
   }
}
