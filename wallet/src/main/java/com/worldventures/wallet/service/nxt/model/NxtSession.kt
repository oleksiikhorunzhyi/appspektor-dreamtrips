package com.worldventures.wallet.service.nxt.model

import com.google.gson.annotations.SerializedName

data class NxtSession(
      @field:SerializedName("nxt_token") val token: String
)
