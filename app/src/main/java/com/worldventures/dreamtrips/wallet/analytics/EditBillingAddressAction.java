package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Card Detail:Edit Billing Address",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class EditBillingAddressAction extends BaseCardDetailsWithDefaultAction {

}
