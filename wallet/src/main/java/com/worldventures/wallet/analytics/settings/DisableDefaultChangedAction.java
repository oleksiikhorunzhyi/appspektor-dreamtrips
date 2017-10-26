package com.worldventures.wallet.analytics.settings;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:disable default payment after:changes saved",
                trackers = AdobeTracker.TRACKER_KEY)
public class DisableDefaultChangedAction extends WalletAnalyticsAction {

   @Attribute("disabledefault") final String disableDefault;
   @Attribute("disabledefaultchange") final String disableDefaultChange = "1";

   public DisableDefaultChangedAction(String disableDefaultTime) {
      this.disableDefault = disableDefaultTime;
   }
}
