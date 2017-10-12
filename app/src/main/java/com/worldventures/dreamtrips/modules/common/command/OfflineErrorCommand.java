package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.core.janet.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class OfflineErrorCommand extends ValueCommandAction<Throwable> {

   public OfflineErrorCommand() {
      super(new RuntimeException("No Internet connection"));
   }

   public OfflineErrorCommand(Throwable value) {
      super(value);
   }
}
