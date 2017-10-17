package com.messenger.messengerservers.chat;

public class AccessConversationDeniedException extends Exception {

   public AccessConversationDeniedException() {
      //default constructor
   }

   public AccessConversationDeniedException(Throwable cause) {
      super(cause);
   }
}
