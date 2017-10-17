package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:security:offline mode",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SettingsOfflineModeScreenAction extends WalletAnalyticsAction {

   @Attribute("offlinemode") final String offlineMode;

   public SettingsOfflineModeScreenAction(boolean offlineModeState) {
      this.offlineMode = offlineModeState ? "Enabled" : "Disabled";
   }

}
