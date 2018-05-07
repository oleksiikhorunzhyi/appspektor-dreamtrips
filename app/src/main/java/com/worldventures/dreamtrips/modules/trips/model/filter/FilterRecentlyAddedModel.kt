package com.worldventures.dreamtrips.modules.trips.model.filter

data class FilterRecentlyAddedModel(private val isRecentlyAdded: Boolean) : BoolFilter(isRecentlyAdded)
