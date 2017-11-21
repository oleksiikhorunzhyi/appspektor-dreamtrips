package com.worldventures.wallet.service.nxt.model

import com.google.gson.annotations.SerializedName

data class MultiResponseElement(
      @field:SerializedName("RefId") val referenceId: String,
      @field:SerializedName("Value") val value: String? = null,
      @field:SerializedName("Error") val error: MultiErrorResponse? = null)