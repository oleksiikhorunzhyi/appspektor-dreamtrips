package com.messenger.delegate.chat.message;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class ChatMessageInteractor {

   private final ActionPipe<ChatSendMessageCommand> sendMessagePipe;
   private final ActionPipe<RetrySendMessageCommand> resendMessagePipe;
   private final ActionPipe<MarkMessageAsReadCommand> markMessageAsReadPipe;

   @Inject
   public ChatMessageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.sendMessagePipe = sessionActionPipeCreator.createPipe(ChatSendMessageCommand.class, Schedulers.io());
      this.resendMessagePipe = sessionActionPipeCreator.createPipe(RetrySendMessageCommand.class, Schedulers.io());
      this.markMessageAsReadPipe = sessionActionPipeCreator.createPipe(MarkMessageAsReadCommand.class, Schedulers.io());
   }

   public ActionPipe<ChatSendMessageCommand> getMessageActionPipe() {
      return sendMessagePipe;
   }

   public ActionPipe<RetrySendMessageCommand> getResendMessagePipe() {
      return resendMessagePipe;
   }

   public ActionPipe<MarkMessageAsReadCommand> getMarkMessageAsReadPipe() {
      return markMessageAsReadPipe;
   }
}
