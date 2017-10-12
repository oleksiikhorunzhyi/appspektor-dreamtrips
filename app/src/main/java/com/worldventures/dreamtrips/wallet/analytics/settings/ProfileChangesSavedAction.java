package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:smartcard profile:changes saved",
                trackers = AdobeTracker.TRACKER_KEY)
public class ProfileChangesSavedAction extends WalletAnalyticsAction {
}
