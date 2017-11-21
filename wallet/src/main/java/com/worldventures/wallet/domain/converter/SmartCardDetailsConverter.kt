package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.ApiSmartCardDetails
import com.worldventures.wallet.domain.entity.SmartCardDetails

import io.techery.mappery.MapperyContext

class SmartCardDetailsConverter : Converter<ApiSmartCardDetails, SmartCardDetails> {

   override fun targetClass() = SmartCardDetails::class.java

   override fun sourceClass() = ApiSmartCardDetails::class.java

   override fun convert(context: MapperyContext, source: ApiSmartCardDetails): SmartCardDetails {
      return SmartCardDetails(
            serialNumber = source.serialNumber(),
            smartCardId = source.scID(),
            bleAddress = source.bleAddress(),
            wvOrderId = source.wvOrderId(),
            revVersion = source.revVersion(),
            nxtOrderId = source.nxtOrderId(),
            orderDate = source.orderDate())
   }
}
