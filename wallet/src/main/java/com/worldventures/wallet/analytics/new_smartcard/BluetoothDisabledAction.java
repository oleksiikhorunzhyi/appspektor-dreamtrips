package com.worldventures.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:error",
                trackers = AdobeTracker.TRACKER_KEY)
public class BluetoothDisabledAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep2ba") final String unAssignCardStep2ba = "1";

   @Attribute("dtaerror") final String dtError = "1";

   @Attribute("errorcondition") final String errorCondition = "Bluetooth not enabled";
}
