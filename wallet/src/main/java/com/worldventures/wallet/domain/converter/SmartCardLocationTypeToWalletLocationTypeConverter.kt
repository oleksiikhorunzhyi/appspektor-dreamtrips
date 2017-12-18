package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType.CONNECT
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType.DISCONNECT
import io.techery.mappery.MapperyContext

class SmartCardLocationTypeToWalletLocationTypeConverter : Converter<SmartCardLocationType, WalletLocationType> {

   override fun sourceClass(): Class<SmartCardLocationType> = SmartCardLocationType::class.java

   override fun targetClass(): Class<WalletLocationType> = WalletLocationType::class.java

   override fun convert(context: MapperyContext, source: SmartCardLocationType): WalletLocationType =
         when (source) {
            SmartCardLocationType.CONNECT -> CONNECT
            else -> DISCONNECT
         }
}
