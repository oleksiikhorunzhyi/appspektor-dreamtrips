package com.worldventures.dreamtrips.modules.infopages.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.infopages.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.command.UploadFeedbackAttachmentCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FeedbackInteractor {

   private ActionPipe<SendFeedbackCommand> sendFeedbackPipe;
   private ActionPipe<UploadFeedbackAttachmentCommand> uploadAttachmentPipe;

   public FeedbackInteractor(SessionActionPipeCreator actionPipeCreator) {
      sendFeedbackPipe = actionPipeCreator.createPipe(SendFeedbackCommand.class, Schedulers.io());
      uploadAttachmentPipe = actionPipeCreator.createPipe(UploadFeedbackAttachmentCommand.class, Schedulers.io());
   }

   public ActionPipe<SendFeedbackCommand> getSendFeedbackPipe() {
      return sendFeedbackPipe;
   }

   public ActionPipe<UploadFeedbackAttachmentCommand> getUploadAttachmentPipe() {
      return uploadAttachmentPipe;
   }
}
