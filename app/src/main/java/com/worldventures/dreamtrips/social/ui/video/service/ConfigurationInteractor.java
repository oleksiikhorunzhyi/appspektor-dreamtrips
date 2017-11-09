package com.worldventures.dreamtrips.social.ui.video.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.video.service.command.ConfigurationCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class ConfigurationInteractor {

   private final ActionPipe<ConfigurationCommand> configurationActionPipe;

   public ConfigurationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.configurationActionPipe = sessionActionPipeCreator.createPipe(ConfigurationCommand.class, Schedulers.immediate());
   }

   public ActionPipe<ConfigurationCommand> configurationActionPipe() {
      return configurationActionPipe;
   }
}
