package com.worldventures.wallet.service.lostcard.command.http.model

import com.google.gson.annotations.SerializedName

data class ApiPlace(
      @field:SerializedName("place_id") val placeId: String,
      @field:SerializedName("name") val name: String,
      @field:SerializedName("vicinity") val vicinity: String
)
