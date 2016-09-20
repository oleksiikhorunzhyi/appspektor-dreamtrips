package com.messenger.delegate.chat;

import com.messenger.delegate.chat.command.KickChatCommand;
import com.messenger.delegate.chat.command.LeaveChatCommand;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class ChatGroupCommandsInteractor {

   private final ActionPipe<LeaveChatCommand> leaveChatPipe;
   private final ActionPipe<KickChatCommand> kickChatPipe;

   @Inject
   public ChatGroupCommandsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      leaveChatPipe = sessionActionPipeCreator.createPipe(LeaveChatCommand.class, Schedulers.io());
      kickChatPipe = sessionActionPipeCreator.createPipe(KickChatCommand.class, Schedulers.io());
   }

   public ActionPipe<LeaveChatCommand> getLeaveChatPipe() {
      return leaveChatPipe;
   }

   public ActionPipe<KickChatCommand> getKickChatPipe() {
      return kickChatPipe;
   }
}

