package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:SmartCard Up to Date",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewSdkVersionAction extends FirmwareAnalyticsAction {
}