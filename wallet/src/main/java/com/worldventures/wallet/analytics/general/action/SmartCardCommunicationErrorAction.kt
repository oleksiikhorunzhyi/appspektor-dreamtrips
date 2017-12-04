package com.worldventures.wallet.analytics.general.action

import com.worldventures.core.service.analytics.ActionPart
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

import java.util.Locale

@AnalyticsEvent(action = "\${navigationState}:error", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SmartCardCommunicationErrorAction : WalletAnalyticsAction {

   @ActionPart internal var navigationState: String

   @Attribute("dtaerror") internal var error = "1"
   @Attribute("errorcode") internal var errorCode: String

   constructor(navigationState: String, requestName: String) {
      this.navigationState = navigationState
      this.errorCode = String.format(Locale.US, "blecomm-%s", requestName)
   }

   constructor(navigationState: String, requestName: String, errorCode: Int) {
      this.navigationState = navigationState
      this.errorCode = String.format(Locale.US, "blecomm-%s-%d", requestName, errorCode)
   }

}
