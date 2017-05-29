package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 2",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class DownloadingUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep2") final String updateStep2 = "1";
}
