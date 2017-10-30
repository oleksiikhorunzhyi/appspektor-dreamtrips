package com.worldventures.wallet.analytics.firmware.action;


import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist:Install",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateInstallAction extends FirmwareAnalyticsAction {
}
