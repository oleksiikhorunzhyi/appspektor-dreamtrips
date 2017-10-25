package com.worldventures.core.janet;

import android.support.annotation.StringRes;

import io.techery.janet.Command;

public abstract class CommandWithError<T> extends Command<T> {

   private String errorMessage;

   @StringRes
   public abstract int getFallbackErrorMessage();

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }
}
