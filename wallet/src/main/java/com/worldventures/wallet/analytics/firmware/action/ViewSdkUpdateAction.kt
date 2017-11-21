package com.worldventures.wallet.analytics.firmware.action

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.FirmwareUpdateData

@AnalyticsEvent(action = "wallet:SmartCard Update:Step 1", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ViewSdkUpdateAction : FirmwareAnalyticsAction() {

   @Attribute("scupdatestep1") internal val updateStep = "1"
   @Attribute("currentversion") internal lateinit var currentVersion: String
   @Attribute("latestversion") internal var latestVersion: String? = null
   @Attribute("dtupdaterqrd") internal var updateRequired: String? = null

   override fun setFirmwareData(data: FirmwareUpdateData) {
      super.setFirmwareData(data)
      val info = data.firmwareInfo
      if (info != null) {
         this.latestVersion = info.firmwareVersion()
         this.updateRequired = if (info.isCompatible) "Yes" else "No"
      }
      this.currentVersion = data.currentFirmwareVersion.nordicAppVersion
   }
}
