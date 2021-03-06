package com.worldventures.core.modules.auth.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LoginCommand;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.api.command.UpdateUserCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class AuthInteractor {

   private final ActionPipe<LogoutCommand> logoutPipe;
   private final ActionPipe<UpdateUserCommand> updateUserPipe;
   private final ActionPipe<LoginCommand> loginActionPipe;

   public AuthInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      updateUserPipe = sessionActionPipeCreator.createPipe(UpdateUserCommand.class, Schedulers.io());
      logoutPipe = sessionActionPipeCreator.createPipe(LogoutCommand.class, Schedulers.io());
      loginActionPipe = sessionActionPipeCreator.createPipe(LoginCommand.class, Schedulers.io());
   }

   public ActionPipe<UpdateUserCommand> updateUserPipe() {
      return updateUserPipe;
   }

   public ActionPipe<LogoutCommand> logoutPipe() {
      return logoutPipe;
   }

   public ActionPipe<LoginCommand> loginActionPipe() {
      return loginActionPipe;
   }
}
