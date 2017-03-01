package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:smartcard profile:changes saved",
                trackers = AdobeTracker.TRACKER_KEY)
public class ProfileChangesSavedAction extends WalletAnalyticsAction {
}
