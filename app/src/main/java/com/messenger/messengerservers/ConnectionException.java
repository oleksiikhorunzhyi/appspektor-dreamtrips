package com.messenger.messengerservers;

public class ConnectionException extends Exception {
   private static final String MESSAGE = "Action cannot be dane without connection";

   public ConnectionException() {
      super(MESSAGE);
   }

   public ConnectionException(Throwable throwable) {
      super(MESSAGE, throwable);
   }
}
