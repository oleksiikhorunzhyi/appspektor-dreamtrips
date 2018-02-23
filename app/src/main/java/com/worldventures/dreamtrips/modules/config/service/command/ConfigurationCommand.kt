package com.worldventures.dreamtrips.modules.config.service.command

import com.worldventures.dreamtrips.modules.config.model.Configuration
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class ConfigurationCommand(private var configuration: Configuration? = null, private val hideTravelConfig: Boolean = false)
   : Command<Configuration>(), CachedAction<Configuration> {

   val videoMaxLength get() = configuration?.videoRequirement?.videoMaxLength

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Configuration>) {
      var tempConfig = configuration

      if (tempConfig == null) tempConfig = Configuration()
      if (hideTravelConfig) callback.onSuccess(tempConfig.copy(travelBannerRequirement = null))
      else callback.onSuccess(tempConfig)
   }

   override fun getCacheData() = result

   override fun onRestore(holder: ActionHolder<*>, cache: Configuration) {
      if (configuration == null) configuration = cache
   }

   override fun getCacheOptions() = CacheOptions()
}
