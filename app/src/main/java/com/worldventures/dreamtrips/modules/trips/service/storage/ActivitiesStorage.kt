package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.modules.trips.model.filter.ActivityModel
import com.worldventures.dreamtrips.modules.trips.service.command.GetActivitiesCommand
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.janet.cache.storage.MemoryStorage

class ActivitiesStorage : MemoryStorage<List<ActivityModel>>(), ActionStorage<List<ActivityModel>> {

   override fun getActionClass() = GetActivitiesCommand::class.java
}
