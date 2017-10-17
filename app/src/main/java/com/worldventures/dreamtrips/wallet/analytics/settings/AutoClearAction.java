package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:auto clear smartcard",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class AutoClearAction extends WalletAnalyticsAction {

   @Attribute("autoclear") final String autoClear;

   public AutoClearAction(String autoClearTime) {
      this.autoClear = autoClearTime;
   }
}
