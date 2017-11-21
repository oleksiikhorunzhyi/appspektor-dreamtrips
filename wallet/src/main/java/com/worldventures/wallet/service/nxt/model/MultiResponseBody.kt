package com.worldventures.wallet.service.nxt.model

import com.google.gson.annotations.SerializedName

data class MultiResponseBody(
      @field:SerializedName("MultiResponseElement") val multiResponseElements: List<MultiResponseElement>
)