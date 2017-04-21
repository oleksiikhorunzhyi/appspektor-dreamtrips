package com.worldventures.dreamtrips.modules.feed.storage.command;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FeedStorageCommand extends FeedItemsStorageBaseCommand {

   public FeedStorageCommand(ListStorageOperation<FeedItem> operation) {
      super(operation);
   }
}
