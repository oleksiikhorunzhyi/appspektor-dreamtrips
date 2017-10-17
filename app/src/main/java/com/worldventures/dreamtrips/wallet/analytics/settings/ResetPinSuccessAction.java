package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:settings:successfully reset your PIN",
                trackers = AdobeTracker.TRACKER_KEY)
public class ResetPinSuccessAction extends WalletAnalyticsAction {

   @Attribute("resetpin2") String resetPin = "1";
}
