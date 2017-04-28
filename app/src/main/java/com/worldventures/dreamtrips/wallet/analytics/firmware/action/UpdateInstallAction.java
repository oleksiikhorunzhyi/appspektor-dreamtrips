package com.worldventures.dreamtrips.wallet.analytics.firmware.action;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:SmartCard Update:Ready Checklist:Install",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateInstallAction extends FirmwareAnalyticsAction {
}
