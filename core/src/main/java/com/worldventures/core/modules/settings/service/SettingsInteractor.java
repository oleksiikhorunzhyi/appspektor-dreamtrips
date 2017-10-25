package com.worldventures.core.modules.settings.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.settings.command.SettingsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class SettingsInteractor {

   private final ActionPipe<SettingsCommand> settingsActionPipe;

   @Inject
   public SettingsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      settingsActionPipe = sessionActionPipeCreator.createPipe(SettingsCommand.class, Schedulers.io());
   }

   public ActionPipe<SettingsCommand> settingsActionPipe() {
      return settingsActionPipe;
   }
}
