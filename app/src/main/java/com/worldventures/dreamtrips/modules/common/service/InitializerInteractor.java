package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.common.command.InitializeCommand;

import javax.inject.Singleton;

import io.techery.janet.ActionPipe;

@Singleton
public class InitializerInteractor {

   private LoginInteractor loginInteractor;
   private ActionPipe<InitializeCommand> initializeCommandActionPipe;

   public InitializerInteractor(SessionActionPipeCreator sessionActionPipeCreator, LoginInteractor loginInteractor) {
      this.loginInteractor = loginInteractor;

      initializeCommandActionPipe = sessionActionPipeCreator.createPipe(InitializeCommand.class);

      connectLoginPipe();
   }

   private void connectLoginPipe() {
      loginInteractor.loginActionPipe().observeSuccess()
            .map(loginCommand -> new InitializeCommand())
            .subscribe(this::send);
   }

   private void send(InitializeCommand initializeCommand) {
      initializeCommandActionPipe.send(initializeCommand);
   }
}
