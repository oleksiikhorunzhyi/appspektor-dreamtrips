package com.worldventures.wallet.service.lostcard.command.http.model

import com.google.gson.annotations.SerializedName

data class AddressRestResponse(
      @field:SerializedName("results") val results: List<ApiAddress>,
      @field:SerializedName("place_id") val placeId: String?,
      @field:SerializedName("status") val status: String
)

data class ApiAddress(
      @field:SerializedName("address_components") val components: List<AddressComponent>,
      @field:SerializedName("formatted_address") val formattedAddress: String,
      @field:SerializedName("types") val types: List<String>
)

data class AddressComponent(
      @field:SerializedName("long_name") val longName: String,
      @field:SerializedName("short_name") val shortName: String,
      @field:SerializedName("types") val types: List<String>)
