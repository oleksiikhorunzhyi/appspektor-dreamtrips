package com.worldventures.dreamtrips.wallet.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Step 5:Update Successful",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateSuccessfulAction extends WalletAnalyticsAction {

   @Attribute("scupdatestep5") final String updateStep5 = "1";

}
