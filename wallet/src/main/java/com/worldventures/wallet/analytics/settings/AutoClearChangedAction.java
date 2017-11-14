package com.worldventures.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:auto clear smartcard:changes saved",
                trackers = AdobeTracker.TRACKER_KEY)
public class AutoClearChangedAction extends WalletAnalyticsAction {

   @Attribute("autoclear") final String autoClear;
   @Attribute("autoclearchange") final String autoClearChange = "1";

   public AutoClearChangedAction(String autoClearTimber) {
      this.autoClear = autoClearTimber;
   }
}
