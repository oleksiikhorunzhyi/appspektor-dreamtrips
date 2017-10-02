package com.worldventures.core.janet;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ValueCommandAction<T> extends CallableCommandAction<T> {

   public ValueCommandAction(T value) {
      super(() -> value);
   }
}
