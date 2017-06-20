package com.worldventures.dreamtrips.modules.version_check.service;

import com.worldventures.dreamtrips.modules.version_check.service.command.ConfigurationCommand;
import com.worldventures.dreamtrips.modules.version_check.service.command.LoadConfigurationCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class AppConfigurationInteractor {

   private ActionPipe<LoadConfigurationCommand> versionCheckActionPipe;
   private ActionPipe<ConfigurationCommand> configurationCommandActionPipe;

   public AppConfigurationInteractor(Janet janet) {
      versionCheckActionPipe = janet.createPipe(LoadConfigurationCommand.class, Schedulers.io());
      configurationCommandActionPipe = janet.createPipe(ConfigurationCommand.class, Schedulers.io());
      subscribeToUpdates();
   }

   public ActionPipe<LoadConfigurationCommand> loadConfigurationPipe() {
      return versionCheckActionPipe;
   }

   public ActionPipe<ConfigurationCommand> configurationCommandActionPipe() {
      return configurationCommandActionPipe;
   }

   private void subscribeToUpdates() {
      versionCheckActionPipe.observeSuccess()
            .map(Command::getResult)
            .subscribe(configuration -> {
               configurationCommandActionPipe.send(new ConfigurationCommand(configuration));
            });
   }
}
