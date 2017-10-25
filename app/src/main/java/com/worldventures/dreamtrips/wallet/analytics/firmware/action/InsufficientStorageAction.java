package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Insufficient Space",
                trackers = AdobeTracker.TRACKER_KEY)
public class InsufficientStorageAction extends FirmwareAnalyticsAction {
}
