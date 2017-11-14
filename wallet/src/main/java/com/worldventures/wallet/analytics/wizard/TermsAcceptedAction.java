package com.worldventures.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions:Agree",
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAcceptedAction extends WalletAnalyticsAction {
   @Attribute("tocagree") final String tocAgree = "1";
}
