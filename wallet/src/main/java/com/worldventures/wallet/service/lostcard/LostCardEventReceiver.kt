package com.worldventures.wallet.service.lostcard

import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType

interface LostCardEventReceiver {

   fun receiveEvent(locationType: WalletLocationType)
}
