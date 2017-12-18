package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType

import io.techery.mappery.MapperyContext

class WalletLocationTypeToSmartCardLocationTypeConverter : Converter<WalletLocationType, SmartCardLocationType> {

   override fun sourceClass(): Class<WalletLocationType> = WalletLocationType::class.java

   override fun targetClass(): Class<SmartCardLocationType> = SmartCardLocationType::class.java

   override fun convert(context: MapperyContext, source: WalletLocationType): SmartCardLocationType =
         when (source) {
            WalletLocationType.CONNECT -> SmartCardLocationType.CONNECT
            else -> SmartCardLocationType.DISCONNECT
         }
}
