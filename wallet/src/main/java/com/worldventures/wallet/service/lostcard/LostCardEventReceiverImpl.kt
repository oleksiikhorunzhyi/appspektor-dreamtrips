package com.worldventures.wallet.service.lostcard

import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.lostcard.command.WalletLocationCommand

internal class LostCardEventReceiverImpl(private val locationInteractor: SmartCardLocationInteractor) : LostCardEventReceiver {

   override fun receiveEvent(locationType: WalletLocationType) {
      locationInteractor.walletLocationCommandPipe().send(WalletLocationCommand(locationType))
   }
}
