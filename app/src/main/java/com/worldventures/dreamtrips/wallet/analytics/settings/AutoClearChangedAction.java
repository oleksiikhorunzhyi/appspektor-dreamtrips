package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:auto clear smartcard:changes saved",
                trackers = AdobeTracker.TRACKER_KEY)
public class AutoClearChangedAction extends WalletAnalyticsAction {

   @Attribute("autoclear") final String autoClear;
   @Attribute("autoclearchange") final String autoClearChange = "1";

   public AutoClearChangedAction(String autoClearTimber) {
      this.autoClear = autoClearTimber;
   }
}
