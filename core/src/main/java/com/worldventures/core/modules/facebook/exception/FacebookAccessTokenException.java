package com.worldventures.core.modules.facebook.exception;


public class FacebookAccessTokenException extends Exception {
   public FacebookAccessTokenException(String message) {
      super(message);
   }

   public FacebookAccessTokenException(String message, Throwable cause) {
      super(message, cause);
   }
}
