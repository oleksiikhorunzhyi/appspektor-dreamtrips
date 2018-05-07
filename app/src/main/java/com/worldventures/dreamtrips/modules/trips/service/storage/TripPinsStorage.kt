package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.trips.model.map.Pin
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsLocationsCommand
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage

open class TripPinsStorage(private val snappyRepository: SnappyRepository) : ActionStorage<List<Pin>> {

   override fun getActionClass() = GetTripsLocationsCommand::class.java

   override fun save(params: CacheBundle?, data: List<Pin>) = snappyRepository.savePins(data)

   override fun get(action: CacheBundle?) = snappyRepository.pins
}
