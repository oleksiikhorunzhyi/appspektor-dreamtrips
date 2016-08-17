package com.worldventures.dreamtrips.modules.feed.service.command;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PostDescriptionCreatedCommand extends Command<String> {

   private String description;

   public PostDescriptionCreatedCommand(String description) {
      this.description = description;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      callback.onSuccess(description);
   }
}