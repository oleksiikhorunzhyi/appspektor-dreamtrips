package com.worldventures.wallet.analytics.firmware.action

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.FirmwareUpdateData

@Suppress("UnsafeCallOnNullableType")
@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 4:Installing Update:Fail", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class RetryInstallUpdateAction(retry: Boolean) : FirmwareAnalyticsAction() {

   @Attribute("updateretry") internal val retry: String = if (retry) "1" else "0"
   @Attribute("currentversion") internal lateinit var currentVersion: String
   @Attribute("latestversion") internal lateinit var latestVersion: String

   override fun setFirmwareData(data: FirmwareUpdateData) {
      super.setFirmwareData(data)
      this.currentVersion = data.currentFirmwareVersion.nordicAppVersion
      this.latestVersion = data.firmwareInfo!!.firmwareVersion()
   }
}
