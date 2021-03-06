package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.command.InitializeCommand;

import io.techery.janet.ActionPipe;

public class InitializerInteractor {

   private final AuthInteractor authInteractor;
   private final ActionPipe<InitializeCommand> initializeCommandActionPipe;

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
