package com.worldventures.dreamtrips.core.navigation.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.navigation.service.command.CloseDialogCommand;

import io.techery.janet.ActionPipe;

public class DialogNavigatorInteractor {

   private final ActionPipe<CloseDialogCommand> closeDialogActionPipe;

   public DialogNavigatorInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.closeDialogActionPipe = sessionActionPipeCreator.createPipe(CloseDialogCommand.class);
   }

   public ActionPipe<CloseDialogCommand> closeDialogActionPipe() {
      return closeDialogActionPipe;
   }
}
