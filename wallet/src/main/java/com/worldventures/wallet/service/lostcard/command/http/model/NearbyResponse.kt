package com.worldventures.wallet.service.lostcard.command.http.model

import com.google.gson.annotations.SerializedName

data class NearbyResponse(@field:SerializedName("results") val locationPlaces: List<ApiPlace>)
