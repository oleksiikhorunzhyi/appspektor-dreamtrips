package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:setup new smartcard:existing card detected:card not connected:error",
                trackers = AdobeTracker.TRACKER_KEY)
public class BluetoothDisabledAction extends WalletAnalyticsAction {
   @Attribute("unassigncardstep2ba")
   final String unAssignCardStep2ba = "1";

   @Attribute("dtaerror")
   final String dtError = "1";

   @Attribute("errorcondition")
   final String errorCondition = "Bluetooth not enabled";
}