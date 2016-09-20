package com.worldventures.dreamtrips.modules.auth.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class AuthInteractor {
   protected ActionPipe<UnsubribeFromPushCommand> unsubribeFromPushPipe;
   protected ActionPipe<UpdateUserCommand> updateUserPipe;

   public AuthInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      unsubribeFromPushPipe = sessionActionPipeCreator.createPipe(UnsubribeFromPushCommand.class);
      updateUserPipe = sessionActionPipeCreator.createPipe(UpdateUserCommand.class, Schedulers.io());
   }

   public ActionPipe<UnsubribeFromPushCommand> unsubribeFromPushPipe() {
      return unsubribeFromPushPipe;
   }

   public ActionPipe<UpdateUserCommand> updateUserPipe() {
      return updateUserPipe;
   }
}
