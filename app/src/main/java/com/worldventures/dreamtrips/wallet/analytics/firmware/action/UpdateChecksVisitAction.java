package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateChecksVisitAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep3") final String udateStep3 = "1";
}
