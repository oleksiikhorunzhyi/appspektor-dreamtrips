package com.worldventures.wallet.util;

public class NoActiveSmartCardException extends RuntimeException {

   public NoActiveSmartCardException(String msg) {
      super(msg);
   }
}
