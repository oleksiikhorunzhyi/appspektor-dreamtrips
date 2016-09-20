package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class LogoutInteractor {

   private final ActionPipe<LogoutCommand> logoutPipe;

   @Inject
   public LogoutInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      logoutPipe =  sessionActionPipeCreator.createPipe(LogoutCommand.class, Schedulers.io());
   }

   public ActionPipe<LogoutCommand> logoutPipe() {
      return logoutPipe;
   }
}
