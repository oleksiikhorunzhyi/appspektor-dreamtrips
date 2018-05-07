package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsByUidCommand
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage

open class TripsByUidsStorage(private val db: SnappyRepository) : ActionStorage<List<TripModel>> {

   override fun getActionClass() = GetTripsByUidCommand::class.java

   override fun save(params: CacheBundle?, data: List<TripModel>) = db.saveTripsDetails(data)

   override fun get(action: CacheBundle?) = db.getTripsDetailsForUids(action?.get(TRIP_UIDS))

   companion object {
      val TRIP_UIDS = "UIDS"
   }
}
