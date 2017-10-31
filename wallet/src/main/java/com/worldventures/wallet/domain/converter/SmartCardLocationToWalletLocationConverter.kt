package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import io.techery.mappery.MapperyContext

class SmartCardLocationToWalletLocationConverter : Converter<SmartCardLocation, WalletLocation> {

   override fun sourceClass(): Class<SmartCardLocation> = SmartCardLocation::class.java

   override fun targetClass(): Class<WalletLocation> = WalletLocation::class.java

   override fun convert(context: MapperyContext, source: SmartCardLocation): WalletLocation {
      return WalletLocation(
            coordinates = if (source.coordinates() != null) context.convert(source.coordinates()!!, WalletCoordinates::class.java) else WalletCoordinates(0.0, 0.0), // todo
            createdAt = source.createdAt(),
            type = context.convert(source.type(), WalletLocationType::class.java))
   }
}
