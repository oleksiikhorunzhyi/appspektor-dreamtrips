package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions:Agree",
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAcceptedAction extends WalletAnalyticsAction {
   @Attribute("tocagree") final String tocAgree = "1";
}
