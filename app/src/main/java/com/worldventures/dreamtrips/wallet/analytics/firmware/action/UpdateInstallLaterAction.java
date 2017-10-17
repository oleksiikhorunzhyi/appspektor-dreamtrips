package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist:Install Later",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateInstallLaterAction extends FirmwareAnalyticsAction {
}
