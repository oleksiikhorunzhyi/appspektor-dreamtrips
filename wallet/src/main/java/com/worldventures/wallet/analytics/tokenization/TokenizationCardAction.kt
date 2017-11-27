package com.worldventures.wallet.analytics.tokenization

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.wallet.analytics.BaseCardDetailsAction
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.wallet.domain.entity.record.Record

open class TokenizationCardAction internal constructor(record: Record, private val actionType: ActionType, private val tokenize: Boolean) : BaseCardDetailsAction() {

   @Attribute("coordinates") internal var coordinates: String? = null

   init {
      fillRecordDetails(record)
   }

   internal fun setCoordinates(coordinates: WalletCoordinates?) {
      if (coordinates != null) this.coordinates = String.format("%s,%s", coordinates.lat, coordinates.lng)
   }

   internal fun generateCondition(): String {
      return String.format("%s Payment Card %s",
            actionType.typeLabel, if (tokenize) "Tokenization" else "Detokenization")
   }

   companion object {

      fun from(record: Record, success: Boolean, actionType: ActionType, tokenize: Boolean): TokenizationCardAction {
         return if (success) {
            TokenizeSuccessAction(record, actionType, tokenize)
         } else {
            TokenizeErrorAction(record, actionType, tokenize)
         }
      }
   }

}
