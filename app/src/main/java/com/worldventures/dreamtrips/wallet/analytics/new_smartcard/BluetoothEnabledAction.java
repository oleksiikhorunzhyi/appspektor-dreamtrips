package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:bluetooth enabled",
                trackers = AdobeTracker.TRACKER_KEY)
public class BluetoothEnabledAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep2bb") final String unAssugnCardStep2bb = "1";
}
