package com.worldventures.dreamtrips.wallet.util;

public class NoActiveSmartCardException extends RuntimeException {

   public NoActiveSmartCardException(String msg) {
      super(msg);
   }
}
