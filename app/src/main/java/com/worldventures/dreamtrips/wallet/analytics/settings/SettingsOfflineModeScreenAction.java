package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:offline mode",
                trackers = AdobeTracker.TRACKER_KEY)
public class SettingsOfflineModeScreenAction extends WalletAnalyticsAction {

   @Attribute("offlinemode") final String offlineMode;

   public SettingsOfflineModeScreenAction(boolean offlineModeState) {
      this.offlineMode = offlineModeState ? "Enabled" : "Disabled";
   }

}
