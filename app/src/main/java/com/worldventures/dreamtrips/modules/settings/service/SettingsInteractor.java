package com.worldventures.dreamtrips.modules.settings.service;

import com.worldventures.dreamtrips.modules.settings.command.SettingsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class SettingsInteractor {

   private final ActionPipe<SettingsCommand> settingsActionPipe;

   @Inject
   public SettingsInteractor(Janet janet) {
      settingsActionPipe = janet.createPipe(SettingsCommand.class, Schedulers.io());
   }

   public ActionPipe<SettingsCommand> settingsActionPipe() {
      return settingsActionPipe;
   }
}
