package com.worldventures.dreamtrips.modules.auth.service;

import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class AuthInteractor {
   protected ActionPipe<UnsubribeFromPushCommand> unsubribeFromPushPipe;
   protected ActionPipe<UpdateUserCommand> updateUserPipe;

   public AuthInteractor(Janet janet) {
      unsubribeFromPushPipe = janet.createPipe(UnsubribeFromPushCommand.class, Schedulers.io());
      updateUserPipe = janet.createPipe(UpdateUserCommand.class, Schedulers.io());
   }

   public ActionPipe<UnsubribeFromPushCommand> unsubribeFromPushPipe() {
      return unsubribeFromPushPipe;
   }

   public ActionPipe<UpdateUserCommand> updateUserPipe() {
      return updateUserPipe;
   }
}
