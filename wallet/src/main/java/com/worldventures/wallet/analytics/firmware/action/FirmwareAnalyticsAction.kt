package com.worldventures.wallet.analytics.firmware.action

import android.support.annotation.CallSuper

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.wallet.domain.entity.FirmwareUpdateData

abstract class FirmwareAnalyticsAction : BaseAnalyticsAction() {

   @Attribute("cid") internal lateinit var cid: String

   @CallSuper
   open fun setFirmwareData(data: FirmwareUpdateData) {
      cid = data.smartCardId()
   }
}
