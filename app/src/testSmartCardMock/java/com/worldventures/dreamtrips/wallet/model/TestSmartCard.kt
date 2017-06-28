package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard

open class TestSmartCard(private val smartCardId: String) : SmartCard() {

   var deviceId: String = "Android1234567890device"
   var cardStatus: CardStatus = SmartCard.CardStatus.ACTIVE

   constructor(smartCardId: String, deviceId: String, cardStatus: CardStatus) : this(smartCardId){
      this.deviceId = deviceId
      this.cardStatus = cardStatus
   }

   override fun cardStatus(): CardStatus = cardStatus

   override fun deviceId(): String = deviceId

   override fun smartCardId(): String = smartCardId
}