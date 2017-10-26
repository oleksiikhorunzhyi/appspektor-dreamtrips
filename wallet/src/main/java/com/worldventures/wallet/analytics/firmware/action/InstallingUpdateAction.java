package com.worldventures.wallet.analytics.firmware.action;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 4:Installing Update",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class InstallingUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep4") final String updateStep4 = "1";
}
