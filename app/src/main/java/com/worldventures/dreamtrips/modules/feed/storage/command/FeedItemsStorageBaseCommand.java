package com.worldventures.dreamtrips.modules.feed.storage.command;

import com.worldventures.dreamtrips.modules.common.list_storage.command.ListStorageCommand;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func2;

@CommandAction
public abstract class FeedItemsStorageBaseCommand extends ListStorageCommand<FeedItem> {

   FeedItemsStorageBaseCommand(ListStorageOperation<FeedItem> operation) {
      super(operation);
   }
}
