package com.worldventures.wallet.analytics.firmware.action;


import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 5:Update Successful",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateSuccessfulAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep5") final String updateStep5 = "1";

}
