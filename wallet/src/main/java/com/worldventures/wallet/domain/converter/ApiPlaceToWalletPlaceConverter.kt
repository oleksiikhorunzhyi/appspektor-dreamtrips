package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace
import com.worldventures.wallet.service.lostcard.command.http.model.ApiPlace

import io.techery.mappery.MapperyContext

class ApiPlaceToWalletPlaceConverter : Converter<ApiPlace, WalletPlace> {

   override fun convert(context: MapperyContext, source: ApiPlace): WalletPlace = WalletPlace(source.name)

   override fun sourceClass(): Class<ApiPlace> = ApiPlace::class.java

   override fun targetClass(): Class<WalletPlace> = WalletPlace::class.java
}
