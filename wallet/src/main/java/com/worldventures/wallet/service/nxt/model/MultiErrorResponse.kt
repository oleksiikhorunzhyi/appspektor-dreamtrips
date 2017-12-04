package com.worldventures.wallet.service.nxt.model

import com.google.gson.annotations.SerializedName

data class MultiErrorResponse(
      @field:SerializedName("Code") val code: Int,
      @field:SerializedName("Message") val message: String
)
