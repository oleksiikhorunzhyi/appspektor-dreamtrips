package com.worldventures.wallet.analytics.new_smartcard

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:error",
      trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class BluetoothDisabledAction : WalletAnalyticsAction() {

   @Attribute("unassigncardstep2ba") internal val unAssignCardStep2ba = "1"
   @Attribute("dtaerror") internal val dtError = "1"
   @Attribute("errorcondition") internal val errorCondition = "Bluetooth not enabled"
}
