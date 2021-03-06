package com.worldventures.core.janet;

import java.util.concurrent.Callable;

import io.techery.janet.Command;

public class CallableCommandAction<T> extends Command<T> {

   private final Callable<T> callable;

   public CallableCommandAction(Callable<T> callable) {
      this.callable = callable;
   }

   @Override
   protected final void run(CommandCallback<T> callback) throws Throwable {
      callback.onSuccess(callable.call());
   }
}
