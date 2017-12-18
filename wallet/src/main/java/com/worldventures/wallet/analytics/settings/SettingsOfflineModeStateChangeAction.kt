package com.worldventures.wallet.analytics.settings

import com.worldventures.core.service.analytics.ActionPart
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AttributeMap
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

import java.util.HashMap

@AnalyticsEvent(action = "wallet:settings:security:offline mode:\${offlineModeState}", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SettingsOfflineModeStateChangeAction(offlineModeState: Boolean) : WalletAnalyticsAction() {

   @ActionPart internal var offlineModeState: String = if (offlineModeState) "enable" else "disable"

   @AttributeMap internal val attributeMap: MutableMap<String, String> = HashMap()

   init {
      attributeMap.put(if (offlineModeState) "offlinemodeenabled" else "offlinemodedisabled", 1.toString())
   }

}
