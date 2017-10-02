package com.worldventures.core.modules.infopages.service.command;

import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AttachmentsRemovedCommand extends Command<List<FeedbackImageAttachment>> {

   private List<FeedbackImageAttachment> attachments;

   public AttachmentsRemovedCommand(List<FeedbackImageAttachment> attachments) {
      this.attachments = attachments;
   }

   @Override
   protected void run(CommandCallback<List<FeedbackImageAttachment>> callback) throws Throwable {
      callback.onSuccess(attachments);
   }
}
