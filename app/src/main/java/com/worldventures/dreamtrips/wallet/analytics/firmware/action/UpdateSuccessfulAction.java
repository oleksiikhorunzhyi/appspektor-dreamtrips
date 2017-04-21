package com.worldventures.dreamtrips.wallet.analytics.firmware.action;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 5:Update Successful",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateSuccessfulAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep5") final String updateStep5 = "1";

}
