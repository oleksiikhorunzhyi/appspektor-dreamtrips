package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions:Agree",
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAcceptedAction extends WalletAnalyticsAction {
   @Attribute("tocagree") final String tocAgree = "1";
}
