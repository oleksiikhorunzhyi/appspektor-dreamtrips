package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.UploadReceiptCommand;

import io.techery.janet.ActionPipe;

public class UploadReceiptInteractor {

   private final ActionPipe<UploadReceiptCommand> uploadReceiptCommandActionPipe;

   public UploadReceiptInteractor(SessionActionPipeCreator actionPipeCreator) {
      this.uploadReceiptCommandActionPipe = actionPipeCreator.createPipe(UploadReceiptCommand.class);
   }

   public ActionPipe<UploadReceiptCommand> uploadReceiptCommandPipe() {
      return uploadReceiptCommandActionPipe;
   }
}
