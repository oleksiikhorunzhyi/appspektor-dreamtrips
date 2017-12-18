package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.location.model.ImmutableSmartCardCoordinates
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates

import io.techery.mappery.MapperyContext

class WalletCoordinatesToSmartCardCoordinatesConverter : Converter<WalletCoordinates, SmartCardCoordinates> {

   override fun sourceClass(): Class<WalletCoordinates> = WalletCoordinates::class.java

   override fun targetClass(): Class<SmartCardCoordinates> = SmartCardCoordinates::class.java

   override fun convert(context: MapperyContext, source: WalletCoordinates): SmartCardCoordinates {
      return ImmutableSmartCardCoordinates.builder()
            .lat(source.lat)
            .lng(source.lng)
            .build()
   }
}
