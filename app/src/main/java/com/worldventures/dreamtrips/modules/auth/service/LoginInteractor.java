package com.worldventures.dreamtrips.modules.auth.service;

import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class LoginInteractor {

   private final ActionPipe<LoginCommand> loginActionPipe;

   @Inject
   public LoginInteractor(Janet janet) {
      this.loginActionPipe = janet.createPipe(LoginCommand.class, Schedulers.io());
   }

   public ActionPipe<LoginCommand> loginActionPipe() {
      return loginActionPipe;
   }
}