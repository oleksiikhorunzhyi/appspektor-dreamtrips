package com.messenger.messengerservers.xmpp.chats;

public class ChatPreconditions {
   private boolean isOwner;

   public ChatPreconditions(boolean isOwner) {
      this.isOwner = isOwner;
   }

   public void checkUserIsOwner() {
      if (!isOwner) throw new IllegalAccessError("You are not owner of chat");
   }

   public void checkUserIsNotOwner() {
      if (isOwner) throw new IllegalAccessError("You are owner of chat");
   }
}
