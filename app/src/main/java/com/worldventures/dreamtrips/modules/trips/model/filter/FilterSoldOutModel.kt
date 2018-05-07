package com.worldventures.dreamtrips.modules.trips.model.filter

data class FilterSoldOutModel(private val isSoldOut: Boolean) : BoolFilter(isSoldOut)
