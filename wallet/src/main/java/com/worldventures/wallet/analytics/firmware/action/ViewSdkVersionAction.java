package com.worldventures.wallet.analytics.firmware.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:SmartCard Up to Date",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewSdkVersionAction extends FirmwareAnalyticsAction {
}
