package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.ConfigurationCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class ConfigurationInteractor {

   private ActionPipe<ConfigurationCommand> configurationActionPipe;

   public ConfigurationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.configurationActionPipe = sessionActionPipeCreator.createPipe(ConfigurationCommand.class, Schedulers.immediate());
   }

   public ActionPipe<ConfigurationCommand> configurationActionPipe() {
      return configurationActionPipe;
   }
}
