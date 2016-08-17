package com.messenger.delegate.command.avatar;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveChatAvatarCommand extends SendChatAvatarCommand {

   public RemoveChatAvatarCommand(String conversationId) {
      super(conversationId, null);
   }
}
