package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress
import com.worldventures.wallet.service.lostcard.command.http.model.AddressComponent
import com.worldventures.wallet.service.lostcard.command.http.model.AddressRestResponse
import io.techery.mappery.MapperyContext

class HttpAddressToWalletAddressConverter : Converter<AddressRestResponse, WalletAddress> {

   override fun sourceClass(): Class<AddressRestResponse> = AddressRestResponse::class.java

   override fun targetClass(): Class<WalletAddress> = WalletAddress::class.java

   override fun convert(context: MapperyContext, source: AddressRestResponse): WalletAddress? {
      if (isResponseInvalid(source)) {
         return null
      }

      val addComponents = getListOfAddressComponents(source)

      return WalletAddress(
            addressLine = getAddress(addComponents),
            countryName = getFieldFromAddressResponse(addComponents, COUNTRY)!!,
            adminArea = getFieldFromAddressResponse(addComponents, ADMINISTRATIVE_AREA_LEVEL_1),
            postalCode = getFieldFromAddressResponse(addComponents, POSTAL_CODE))
   }

   private fun getListOfAddressComponents(addressRestResponse: AddressRestResponse): ArrayList<AddressComponent> {
      return addressRestResponse.results
            .fold(ArrayList()) { addComponents, addressComponents ->
               addComponents.addAll(addressComponents.components)
               addComponents
            }
   }

   private fun isResponseInvalid(addressRestResponse: AddressRestResponse) =
         (addressRestResponse.status != "OK" || addressRestResponse.results.isEmpty())

   private fun getAddress(addressComponents: ArrayList<AddressComponent>): String {
      val number = getFieldFromAddressResponse(addressComponents, STREET_NUMBER)
      val street = getFieldFromAddressResponse(addressComponents, ROUTE)
      val addressBuilder = StringBuilder()
      if (number != null) {
         addressBuilder.append(number).append(", ")
      }
      addressBuilder.append(street)
      return addressBuilder.toString()
   }

   private fun getFieldFromAddressResponse(components: List<AddressComponent>, fieldName: String): String? =
         components.filter { element -> element.types.contains(fieldName) }
               .map { element -> element.longName }
               .firstOrNull()

   companion object {
      private val STREET_NUMBER = "street_number"
      private val ROUTE = "route"
      private val COUNTRY = "country"
      private val ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1"
      private val POSTAL_CODE = "postal_code"
   }
}
