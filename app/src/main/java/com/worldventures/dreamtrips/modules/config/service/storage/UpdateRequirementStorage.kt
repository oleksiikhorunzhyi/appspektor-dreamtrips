package com.worldventures.dreamtrips.modules.config.service.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.config.model.Configuration
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand

class UpdateRequirementStorage(private val snappyRepository: SnappyRepository) : ActionStorage<Configuration> {

   override fun getActionClass() = ConfigurationCommand::class.java

   override fun save(params: CacheBundle?, data: Configuration) = snappyRepository.saveAppUpdateRequirement(data)

   override fun get(action: CacheBundle?) = snappyRepository.appUpdateRequirement
}
