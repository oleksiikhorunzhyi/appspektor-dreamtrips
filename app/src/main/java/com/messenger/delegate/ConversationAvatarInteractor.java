package com.messenger.delegate;

import com.messenger.delegate.command.avatar.RemoveChatAvatarCommand;
import com.messenger.delegate.command.avatar.SendChatAvatarCommand;
import com.messenger.delegate.command.avatar.SetChatAvatarCommand;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class ConversationAvatarInteractor {
   private final ActionPipe<SendChatAvatarCommand> sendChatAvatarCommandActionPipe;
   private final ActionPipe<SetChatAvatarCommand> setChatAvatarCommandPipe;
   private final ActionPipe<RemoveChatAvatarCommand> removeChatAvatarCommandPipe;

   @Inject
   ConversationAvatarInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.sendChatAvatarCommandActionPipe = sessionActionPipeCreator.createPipe(SendChatAvatarCommand.class, Schedulers.io());
      this.setChatAvatarCommandPipe = sessionActionPipeCreator.createPipe(SetChatAvatarCommand.class, Schedulers.io());
      this.removeChatAvatarCommandPipe = sessionActionPipeCreator.createPipe(RemoveChatAvatarCommand.class, Schedulers.io());
   }

   public ActionPipe<SendChatAvatarCommand> getSendChatAvatarCommandPipe() {
      return sendChatAvatarCommandActionPipe;
   }

   public ActionPipe<SetChatAvatarCommand> getSetChatAvatarCommandPipe() {
      return setChatAvatarCommandPipe;
   }

   public ActionPipe<RemoveChatAvatarCommand> getRemoveChatAvatarCommandPipe() {
      return removeChatAvatarCommandPipe;
   }
}
