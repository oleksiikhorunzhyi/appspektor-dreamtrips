package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 4:Installing Update",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class InstallingUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep4") final String updateStep4 = "1";
}
