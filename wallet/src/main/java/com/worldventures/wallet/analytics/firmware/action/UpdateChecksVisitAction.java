package com.worldventures.wallet.analytics.firmware.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateChecksVisitAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep3") final String udateStep3 = "1";
}
