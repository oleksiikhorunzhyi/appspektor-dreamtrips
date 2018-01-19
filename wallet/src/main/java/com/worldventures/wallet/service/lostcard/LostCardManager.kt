package com.worldventures.wallet.service.lostcard

internal interface LostCardManager {

   fun connect(smartCardId: String)

   fun disconnect()
}
