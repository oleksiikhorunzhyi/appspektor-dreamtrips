package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:auto clear smartcard",
                trackers = AdobeTracker.TRACKER_KEY)
public class AutoClearAction extends WalletAnalyticsAction {

   @Attribute("autoclear") final String autoClear;

   public AutoClearAction(String autoClearTime) {
      this.autoClear = autoClearTime;
   }
}
