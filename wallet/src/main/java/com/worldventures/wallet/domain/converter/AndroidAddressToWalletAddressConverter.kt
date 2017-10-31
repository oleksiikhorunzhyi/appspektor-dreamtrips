package com.worldventures.wallet.domain.converter

import android.location.Address

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress

import io.techery.mappery.MapperyContext

class AndroidAddressToWalletAddressConverter : Converter<Address, WalletAddress> {

   override fun sourceClass(): Class<Address> = Address::class.java

   override fun targetClass(): Class<WalletAddress> = WalletAddress::class.java

   override fun convert(context: MapperyContext, source: Address): WalletAddress
         = WalletAddress(source.getAddressLine(0), source.countryName, source.adminArea, source.postalCode)
}
