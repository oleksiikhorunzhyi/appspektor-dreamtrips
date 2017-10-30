package com.worldventures.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:settings:successfully reset your PIN",
                trackers = AdobeTracker.TRACKER_KEY)
public class ResetPinSuccessAction extends WalletAnalyticsAction {

   @Attribute("resetpin2") String resetPin = "1";
}
