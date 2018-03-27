package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripDetailsCommand
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage

class TripDetailsStorage(private val db: SnappyRepository) : ActionStorage<TripModel> {

   override fun getActionClass() = GetTripDetailsCommand::class.java

   override fun save(params: CacheBundle?, data: TripModel) = db.saveTripDetails(data)

   override fun get(action: CacheBundle?) = db.getTripDetail(action?.get(UID))

   companion object {
      val UID = "UID"
   }
}
