package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.ActionPart;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "wallet:settings:security:offline mode:${offlineModeState}",
                trackers = AdobeTracker.TRACKER_KEY)
public class SettingsOfflineModeStateChangeAction extends WalletAnalyticsAction {

   @ActionPart String offlineModeState;

   @AttributeMap final Map<String, String> attributeMap = new HashMap<>();

   public SettingsOfflineModeStateChangeAction(boolean offlineModeState) {
      this.offlineModeState = offlineModeState ? "enable" : "disable";
      attributeMap.put(offlineModeState ? "offlinemodeenabled" : "offlinemodedisabled", String.valueOf(1));
   }

}
