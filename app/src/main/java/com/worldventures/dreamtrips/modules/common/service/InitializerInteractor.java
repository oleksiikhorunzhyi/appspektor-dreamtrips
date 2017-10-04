package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.command.InitializeCommand;

import io.techery.janet.ActionPipe;

public class InitializerInteractor {

   private AuthInteractor authInteractor;
   private ActionPipe<InitializeCommand> initializeCommandActionPipe;

   public InitializerInteractor(SessionActionPipeCreator sessionActionPipeCreator, AuthInteractor authInteractor) {
      this.authInteractor = authInteractor;
      initializeCommandActionPipe = sessionActionPipeCreator.createPipe(InitializeCommand.class);
      connectLoginPipe();
   }

   private void connectLoginPipe() {
      authInteractor.loginActionPipe().observeSuccess()
            .map(loginCommand -> new InitializeCommand())
            .subscribe(this::send);
   }

   private void send(InitializeCommand initializeCommand) {
      initializeCommandActionPipe.send(initializeCommand);
   }
}
