package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:disable default payment after",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class DisableDefaultAction extends WalletAnalyticsAction {

   @Attribute("disabledefault") final String disableDefault;

   public DisableDefaultAction(String disableDefaultTime) {
      this.disableDefault = disableDefaultTime;
   }
}
