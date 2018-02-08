package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import rx.functions.Action1
import rx.functions.Func0

open class TripsDiskStorage(private val db: SnappyRepository) : PaginatedDiskStorage<TripModel>() {

   override fun getRestoreAction() = Func0 { db.trips }

   override fun getSaveAction() = Action1<List<TripModel>> { db.saveTrips(it) }

}
