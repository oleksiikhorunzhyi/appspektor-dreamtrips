package com.worldventures.wallet.service.nxt.model

import com.google.gson.annotations.SerializedName

data class MultiRequestBody(
      @field:SerializedName("MultiRequestElement") val multiRequestElements: List<MultiRequestElement>,
      @field:SerializedName("SessionToken") val sessionToken: String? = null

)
