package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardCoordinates
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import io.techery.mappery.MapperyContext

class SmartCardCoordinatesToWalletCoordinatesConverter : Converter<SmartCardCoordinates, WalletCoordinates> {

   override fun sourceClass(): Class<SmartCardCoordinates> = SmartCardCoordinates::class.java

   override fun targetClass(): Class<WalletCoordinates> = WalletCoordinates::class.java

   override fun convert(context: MapperyContext, source: SmartCardCoordinates): WalletCoordinates =
         WalletCoordinates(source.lat(), source.lng())
}
