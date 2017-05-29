package com.worldventures.dreamtrips.modules.infopages.service;

import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.techery.janet.ActionPipe;

public class CancelableFeedbackAttachmentsManager extends FeedbackAttachmentsManager {

   private final ActionPipe<UploadFeedbackAttachmentCommand> uploadAttachmentPipe;
   private final List<UploadFeedbackAttachmentCommand> currentCommands = new CopyOnWriteArrayList<>();

   public CancelableFeedbackAttachmentsManager(ActionPipe<UploadFeedbackAttachmentCommand> uploadAttachmentPipe) {
      this.uploadAttachmentPipe = uploadAttachmentPipe;
   }

   public void send(String uri) {
      final UploadFeedbackAttachmentCommand command = new UploadFeedbackAttachmentCommand(new FeedbackImageAttachment(uri));
      currentCommands.add(command);
      uploadAttachmentPipe.send(command);
   }

   public void onCommandFinished(UploadFeedbackAttachmentCommand command) {
      currentCommands.remove(command);
   }

   public void cancelAll() {
      for (UploadFeedbackAttachmentCommand command : currentCommands) {
         uploadAttachmentPipe.cancel(command);
      }
      currentCommands.clear();
      uploadAttachmentPipe.clearReplays();
   }
}
