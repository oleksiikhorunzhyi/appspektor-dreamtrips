package com.worldventures.dreamtrips.modules.trips.service.storage

import com.worldventures.dreamtrips.modules.trips.model.filter.RegionModel
import com.worldventures.dreamtrips.modules.trips.service.command.GetRegionsCommand
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.janet.cache.storage.MemoryStorage

class RegionsStorage : MemoryStorage<List<RegionModel>>(), ActionStorage<List<RegionModel>> {

   override fun getActionClass() = GetRegionsCommand::class.java
}
