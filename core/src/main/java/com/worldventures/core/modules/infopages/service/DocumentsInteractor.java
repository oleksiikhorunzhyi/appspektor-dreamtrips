package com.worldventures.core.modules.infopages.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class DocumentsInteractor {

   private final ActionPipe<GetDocumentsCommand> getDocumentsActionPipe;

   public DocumentsInteractor(SessionActionPipeCreator actionPipeCreator) {
      this.getDocumentsActionPipe = actionPipeCreator.createPipe(GetDocumentsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetDocumentsCommand> getDocumentsActionPipe() {
      return getDocumentsActionPipe;
   }
}
