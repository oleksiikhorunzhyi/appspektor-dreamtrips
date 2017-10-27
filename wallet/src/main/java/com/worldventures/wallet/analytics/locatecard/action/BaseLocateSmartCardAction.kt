package com.worldventures.wallet.analytics.locatecard.action

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.wallet.analytics.WalletAnalyticsAction
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates

import java.util.Locale

abstract class BaseLocateSmartCardAction : WalletAnalyticsAction() {

   @Attribute("coordinates") internal var coordinates: String? = null

   open fun setLocation(walletCoordinates: WalletCoordinates) {
      this.coordinates = String.format(Locale.US, "%f,%f", walletCoordinates.lat(), walletCoordinates.lng())
   }
}
