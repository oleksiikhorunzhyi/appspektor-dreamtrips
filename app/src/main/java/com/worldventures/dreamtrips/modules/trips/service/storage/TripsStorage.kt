package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsCommand
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.janet.cache.storage.CombinedListStorage
import com.worldventures.janet.cache.storage.Storage

class TripsStorage(memoryStorage: Storage<List<TripModel>>, diskStorage: Storage<List<TripModel>>) :
      CombinedListStorage<TripModel>(memoryStorage, diskStorage), ActionStorage<List<TripModel>> {

   override fun getActionClass() = GetTripsCommand::class.java
}

