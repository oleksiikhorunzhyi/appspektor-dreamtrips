package com.worldventures.dreamtrips.core.navigation.service.command;

import com.worldventures.core.janet.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CloseDialogCommand extends ValueCommandAction<Void> {

   public CloseDialogCommand() {
      super(null);
   }
}