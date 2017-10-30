package com.worldventures.core.service.analytics;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.worldventures.core.service.ConnectionInfoProvider;
import com.worldventures.core.service.DeviceInfoProvider;
import com.worldventures.core.utils.DateTimeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdobeTracker extends Tracker {

   private static final String TIME_FORMAT_ANALYTICS = "HH:mm:ss";

   public static final String TRACKER_KEY = "adobe_tracker";
   private static final String DEFAULT_PREFIX = "dta:";
   private static final String CHANNEL_KEY = "channel";
   private static final String TIME_PARTING = "timeparting";
   private static final String CHANNEL_VALUE = "App:Dreamtrips";
   private static final String ACTION = "action";
   private static final String PREV_VIEW_STATE = "previouscreen";
   private static final String DEVICE_ID = "deviceid";
   private static final String WIFI_CONNECTED = "wifi";

   private final ConnectionInfoProvider connectionInfoProvider;
   private final DeviceInfoProvider deviceInfoProvider;
   private final boolean debugLogging;
   private String lastTrackedViewState;

   public AdobeTracker(DeviceInfoProvider deviceInfoProvider, ConnectionInfoProvider connectionInfoProvider, boolean debugLogging) {
      this.connectionInfoProvider = connectionInfoProvider;
      this.deviceInfoProvider = deviceInfoProvider;
      this.debugLogging = debugLogging;
   }

   @Override
   public String getKey() {
      return TRACKER_KEY;
   }

   @Override
   public void onCreate(@Nullable Activity activity) {
      if (checkNullAndWarn(activity)) { return; }
      Config.setDebugLogging(debugLogging);
      Config.setContext(activity.getApplicationContext());
   }

   @Override
   public void onResume(@Nullable Activity activity) {
      if (checkNullAndWarn(activity)) { return; }
      Config.collectLifecycleData(activity);
   }

   @Override
   public void onPause(@Nullable Activity activity) {
      if (checkNullAndWarn(activity)) { return; }
      Config.pauseCollectingLifecycleData();
   }

   @Override
   public void trackEvent(String category, String viewState, Map<String, Object> data) {
      Map<String, Object> contextData = new HashMap<>();
      if (data != null) {
         contextData.putAll(data);
      }
      if (getHeaderData() != null) { contextData.putAll(getHeaderData()); }

      String preparedViewState = prepareViewState(viewState);

      contextData.put(CHANNEL_KEY, CHANNEL_VALUE);
      contextData.put(PREV_VIEW_STATE, lastTrackedViewState);
      contextData.put(ACTION, preparedViewState);
      contextData.put(TIME_PARTING, DateTimeUtils.convertDateToString(new Date(), TIME_FORMAT_ANALYTICS));
      contextData.put(WIFI_CONNECTED, connectionInfoProvider.isWifi() ? "Yes" : "No");
      contextData.put(DEVICE_ID, deviceInfoProvider.getUniqueIdentifier());

      Analytics.trackState(preparedViewState, contextData);
      lastTrackedViewState = preparedViewState;
   }

   private String prepareViewState(String viewState) {
      return DEFAULT_PREFIX + viewState;
   }
}
