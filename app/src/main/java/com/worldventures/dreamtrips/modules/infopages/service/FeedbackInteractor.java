package com.worldventures.dreamtrips.modules.infopages.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.infopages.service.command.AttachmentsRemovedCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FeedbackInteractor {

   private final ActionPipe<GetFeedbackCommand> getFeedbackPipe;
   private ActionPipe<SendFeedbackCommand> sendFeedbackPipe;
   private ActionPipe<UploadFeedbackAttachmentCommand> uploadAttachmentPipe;
   private ActionPipe<AttachmentsRemovedCommand> attachmentsRemovedPipe;

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
