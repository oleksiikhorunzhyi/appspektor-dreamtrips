package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:settings:successfully reset your PIN",
                trackers = AdobeTracker.TRACKER_KEY)
public class ResetPinSuccessAction extends WalletAnalyticsAction {

   @Attribute("resetpin2") String resetPin = "1";
}
