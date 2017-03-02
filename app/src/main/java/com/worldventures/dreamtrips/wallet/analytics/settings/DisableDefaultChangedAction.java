package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:disable default payment after:changes saved",
                trackers = AdobeTracker.TRACKER_KEY)
public class DisableDefaultChangedAction extends WalletAnalyticsAction {

   @Attribute("disabledefault") final String disableDefault;
   @Attribute("disabledefaultchange") String disableDefaultChange = "1";

   public DisableDefaultChangedAction(long disableDefault) {
      this.disableDefault = Long.toString(disableDefault);
   }
}
