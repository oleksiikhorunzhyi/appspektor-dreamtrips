package com.worldventures.dreamtrips.modules.config.service

import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand
import com.worldventures.dreamtrips.modules.config.service.command.LoadConfigurationCommand

import io.techery.janet.ActionPipe
import io.techery.janet.Janet
import rx.schedulers.Schedulers

class AppConfigurationInteractor(janet: Janet) {

   val loadConfigPipe: ActionPipe<LoadConfigurationCommand> = janet.createPipe(LoadConfigurationCommand::class.java, Schedulers.io())
   val configurationPipe: ActionPipe<ConfigurationCommand> = janet.createPipe(ConfigurationCommand::class.java, Schedulers.io())

   init {
      subscribeToUpdates()
   }

   private fun subscribeToUpdates() {
      loadConfigPipe.observeSuccess()
            .map { it.result }
            .subscribe { configurationPipe.send(ConfigurationCommand(it)) }
   }
}
