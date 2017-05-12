package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateChecksVisitAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep3") final String udateStep3 = "1";
}
