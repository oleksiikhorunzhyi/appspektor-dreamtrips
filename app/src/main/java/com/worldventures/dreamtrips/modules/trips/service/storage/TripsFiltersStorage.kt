package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsFilterDataCommand
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage

class TripsFiltersStorage(private val snappyRepository: SnappyRepository) : ActionStorage<CachedTripFilters> {

   override fun save(params: CacheBundle?, data: CachedTripFilters) = snappyRepository.saveTripFilters(data)

   override fun get(action: CacheBundle?) = snappyRepository.tripFilters

   override fun getActionClass() = GetTripsFilterDataCommand::class.java
}
