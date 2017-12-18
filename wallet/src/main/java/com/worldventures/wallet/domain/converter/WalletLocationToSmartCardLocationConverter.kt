package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardLocation
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation

import io.techery.mappery.MapperyContext

class WalletLocationToSmartCardLocationConverter : Converter<WalletLocation, SmartCardLocation> {

   override fun sourceClass(): Class<WalletLocation> = WalletLocation::class.java

   override fun targetClass(): Class<SmartCardLocation> = SmartCardLocation::class.java

   override fun convert(context: MapperyContext, source: WalletLocation): SmartCardLocation {
      return ImmutableSmartCardLocation.builder()
            .coordinates(context.convert(source.coordinates, SmartCardCoordinates::class.java))
            .createdAt(source.createdAt)
            .type(context.convert(source.type, SmartCardLocationType::class.java))
            .build()
   }
}
