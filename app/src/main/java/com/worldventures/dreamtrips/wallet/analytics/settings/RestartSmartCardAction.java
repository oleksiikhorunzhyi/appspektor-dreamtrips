package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:restart smart card",
                trackers = AdobeTracker.TRACKER_KEY)
public class RestartSmartCardAction extends WalletAnalyticsAction {

   @Attribute("restartcard") String restartCard = "1";
}
