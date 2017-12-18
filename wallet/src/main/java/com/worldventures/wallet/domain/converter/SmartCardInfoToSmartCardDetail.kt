package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo
import com.worldventures.wallet.domain.entity.SmartCardDetails
import io.techery.mappery.MapperyContext

internal class SmartCardInfoToSmartCardDetail : Converter<SmartCardInfo, SmartCardDetails> {

   override fun sourceClass() = SmartCardInfo::class.java

   override fun targetClass() = SmartCardDetails::class.java

   override fun convert(context: MapperyContext, source: SmartCardInfo): SmartCardDetails {
      return SmartCardDetails(
            smartCardId = source.scId(),
            serialNumber = source.serialNumber(),
            bleAddress = source.bleAddress(),
            revVersion = source.revVersion(),
            wvOrderId = source.wvOrderId(),
            nxtOrderId = source.nxtOrderId(),
            orderDate = source.orderDate())
   }
}
