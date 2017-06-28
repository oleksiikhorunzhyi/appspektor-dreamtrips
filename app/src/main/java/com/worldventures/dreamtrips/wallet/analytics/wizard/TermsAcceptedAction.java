package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions:Agree",
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAcceptedAction extends WalletAnalyticsAction {
   @Attribute("tocagree") final String tocAgree = "1";
}
