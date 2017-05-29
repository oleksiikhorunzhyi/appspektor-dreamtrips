package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
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
