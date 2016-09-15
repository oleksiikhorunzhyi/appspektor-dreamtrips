package com.messenger.delegate.chat;

import com.messenger.delegate.chat.command.KickChatCommand;
import com.messenger.delegate.chat.command.LeaveChatCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class ChatGroupCommandsInteractor {

   private final ActionPipe<LeaveChatCommand> leaveChatPipe;
   private final ActionPipe<KickChatCommand> kickChatPipe;

   @Inject
   public ChatGroupCommandsInteractor(Janet janet) {
      leaveChatPipe = janet.createPipe(LeaveChatCommand.class, Schedulers.io());
      kickChatPipe = janet.createPipe(KickChatCommand.class, Schedulers.io());
   }

   public ActionPipe<LeaveChatCommand> getLeaveChatPipe() {
      return leaveChatPipe;
   }

   public ActionPipe<KickChatCommand> getKickChatPipe() {
      return kickChatPipe;
   }
}

