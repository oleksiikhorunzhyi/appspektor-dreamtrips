package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 4:Installing Update",
                trackers = AdobeTracker.TRACKER_KEY)
public class InstallingUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep4") final String updateStep4 = "1";
}
