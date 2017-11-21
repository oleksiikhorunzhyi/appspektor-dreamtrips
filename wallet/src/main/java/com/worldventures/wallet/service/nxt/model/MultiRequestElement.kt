package com.worldventures.wallet.service.nxt.model

import com.google.gson.annotations.SerializedName

data class MultiRequestElement(
      @field:SerializedName("Operation") val operation: String,
      @field:SerializedName("TokenName") val tokenName: String,
      @field:SerializedName("Value") val value: String,
      @field:SerializedName("RefId") val referenceId: String
)