package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.ConnectionStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import com.worldventures.wallet.domain.entity.SmartCardStatus

abstract class WalletAnalyticsAction : BaseAnalyticsAction {

   @Attribute("cid") internal var cid: String? = null
   @Attribute("lockstatus") internal var lockStatus: String? = null
   @Attribute("cardconnected") internal var cardConnected: String? = null
   @Attribute("batterystatus") internal var batteryLevel: String? = null
   @Attribute("currentversion") internal var currentVersion: String? = null

   constructor()

   constructor(scId: String) {
      cid = scId
   }

   internal fun setSmartCardAction(smartCard: SmartCard?, smartCardStatus: SmartCardStatus,
                                   smartCardFirmware: SmartCardFirmware?) {
      setSmartCardData(
            fetchScId(smartCard),
            fetchScStatus(smartCard),
            fetchConnectionStatus(smartCardStatus),
            fetchLockState(smartCardStatus),
            fetchBatteryLevel(smartCardStatus))

      if (smartCardFirmware != null) {
         setCurrentVersion(smartCardFirmware.nordicAppVersion)
      }
   }

   private fun fetchScId(smartCard: SmartCard?) = smartCard?.smartCardId

   private fun fetchScStatus(smartCard: SmartCard?) = smartCard?.cardStatus

   private fun fetchConnectionStatus(smartCardStatus: SmartCardStatus) = smartCardStatus.connectionStatus

   private fun fetchLockState(smartCardStatus: SmartCardStatus) = smartCardStatus.lock

   private fun fetchBatteryLevel(smartCardStatus: SmartCardStatus) = smartCardStatus.batteryLevel

   private fun setSmartCardData(scId: String?, cardState: CardStatus?,
                                connectionStatus: ConnectionStatus, lockStatus: Boolean, batteryLevel: Int) {

      setSmartCardId(scId)
      setConnected(connectionStatus.isConnected)

      if (connectionStatus.isConnected && cardState != null && cardState.isActive) {
         setLockStatus(lockStatus)
         setBatteryLevel(batteryLevel)
      }
   }

   private fun setSmartCardId(scId: String?) {
      cid = scId
   }

   protected fun setConnected(connected: Boolean) {
      cardConnected = if (connected) "Yes" else "No"
   }

   private fun setLockStatus(lockStatus: Boolean) {
      this.lockStatus = if (lockStatus) "Locked" else "Unlocked"
   }

   private fun setBatteryLevel(batteryLevel: Int) {
      this.batteryLevel = if (batteryLevel != 0) Integer.toString(batteryLevel) else null
   }

   private fun setCurrentVersion(currentVersion: String) {
      this.currentVersion = currentVersion
   }
}
