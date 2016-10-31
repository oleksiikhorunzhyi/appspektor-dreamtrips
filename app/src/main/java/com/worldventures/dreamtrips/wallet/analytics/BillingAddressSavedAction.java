package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:Card Detail:Save Changes",
                trackers = AdobeTracker.TRACKER_KEY)
public class BillingAddressSavedAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("editsaved") final String editSaved = "1";
}
