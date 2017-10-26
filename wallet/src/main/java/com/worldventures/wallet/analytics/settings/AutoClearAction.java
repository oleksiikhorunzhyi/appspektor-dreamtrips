package com.worldventures.wallet.analytics.settings;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:auto clear smartcard",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class AutoClearAction extends WalletAnalyticsAction {

   @Attribute("autoclear") final String autoClear;

   public AutoClearAction(String autoClearTime) {
      this.autoClear = autoClearTime;
   }
}
