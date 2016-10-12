package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;

import java.util.HashMap;
import java.util.Map;

public class AdobeTracker extends Tracker {

   public static final String TRACKER_KEY = "adobe_tracker";
   private static final String DEFAULT_PREFIX = "dta:";
   private static final String CHANNEL_KEY = "channel";
   private static final String CHANNEL_VALUE = "App:Dreamtrips";
   private static final String ACTION = "action";
   private static final String PREV_VIEW_STATE = "previouscreen";
   private static final String DEVICE_ID = "deviceid";
   private static final String WIFI_CONNECTED = "wifi";

   private String lastTrackedViewState;

   private ConnectionInfoProvider connectionInfoProvider;
   private DeviceInfoProvider deviceInfoProvider;

   public AdobeTracker(DeviceInfoProvider deviceInfoProvider, ConnectionInfoProvider connectionInfoProvider) {
      this.connectionInfoProvider = connectionInfoProvider;
      this.deviceInfoProvider = deviceInfoProvider;
   }

   @Override
   public String getKey() {
      return TRACKER_KEY;
   }

   @Override
   public void onCreate(@Nullable Activity activity) {
      if (checkNullAndWarn(activity)) return;
      Config.setDebugLogging(BuildConfig.DEBUG);
      Config.setContext(activity.getApplicationContext());
   }

   @Override
   public void onResume(@Nullable Activity activity) {
      if (checkNullAndWarn(activity)) return;
      Config.collectLifecycleData(activity);
   }

   @Override
   public void onPause(@Nullable Activity activity) {
      if (checkNullAndWarn(activity)) return;
      Config.pauseCollectingLifecycleData();
   }

   @Override
   public void trackEvent(String category, String viewState, Map<String, Object> data) {
      if (data == null) data = new HashMap<>();
      if (headerData != null) data.putAll(headerData);

      String preparedViewState = prepareViewState(viewState);

      data.put(CHANNEL_KEY, CHANNEL_VALUE);
      data.put(PREV_VIEW_STATE, lastTrackedViewState);
      data.put(ACTION, preparedViewState);
      data.put(WIFI_CONNECTED, connectionInfoProvider.isWifi() ? "Yes" : "No");
      data.put(DEVICE_ID, deviceInfoProvider.getUniqueIdentifier());

      Analytics.trackState(preparedViewState, data);
      lastTrackedViewState = preparedViewState;
   }

   private String prepareViewState(String viewState) {
      return DEFAULT_PREFIX + viewState;
   }
}
