package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:restart smart card",
                trackers = AdobeTracker.TRACKER_KEY)
public class RestartSmartCardAction extends WalletAnalyticsAction {

   @Attribute("restartcard") String restartCard = "1";
}
