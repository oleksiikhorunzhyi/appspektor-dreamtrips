package com.worldventures.dreamtrips.social.ui.feed.storage.command;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.command.annotations.CommandAction;

@SuppressWarnings("PMD.UseUtilityClass")
@CommandAction
public final class FeedStorageCommand extends FeedItemsStorageBaseCommand {

   public static FeedStorageCommand dummyCommand() {
      return new FeedStorageCommand(null);
   }

   public FeedStorageCommand(@Nullable ListStorageOperation<FeedItem> operation) {
      super(operation);
   }
}
