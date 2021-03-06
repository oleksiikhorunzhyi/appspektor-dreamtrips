package com.worldventures.core.modules.infopages.service;


import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.infopages.service.command.AttachmentsRemovedCommand;
import com.worldventures.core.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.core.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.core.modules.infopages.service.command.UploadFeedbackAttachmentCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FeedbackInteractor {

   private final ActionPipe<GetFeedbackCommand> getFeedbackPipe;
   private final ActionPipe<SendFeedbackCommand> sendFeedbackPipe;
   private final ActionPipe<UploadFeedbackAttachmentCommand> uploadAttachmentPipe;
   private final ActionPipe<AttachmentsRemovedCommand> attachmentsRemovedPipe;

   public FeedbackInteractor(SessionActionPipeCreator actionPipeCreator) {
      sendFeedbackPipe = actionPipeCreator.createPipe(SendFeedbackCommand.class, Schedulers.io());
      uploadAttachmentPipe = actionPipeCreator.createPipe(UploadFeedbackAttachmentCommand.class, Schedulers.io());
      attachmentsRemovedPipe = actionPipeCreator.createPipe(AttachmentsRemovedCommand.class, Schedulers.io());
      getFeedbackPipe = actionPipeCreator.createPipe(GetFeedbackCommand.class, Schedulers.io());
   }

   public ActionPipe<GetFeedbackCommand> getFeedbackPipe() {
      return getFeedbackPipe;
   }

   public ActionPipe<SendFeedbackCommand> sendFeedbackPipe() {
      return sendFeedbackPipe;
   }

   public ActionPipe<UploadFeedbackAttachmentCommand> uploadAttachmentPipe() {
      return uploadAttachmentPipe;
   }

   public ActionPipe<AttachmentsRemovedCommand> attachmentsRemovedPipe() {
      return attachmentsRemovedPipe;
   }
}
