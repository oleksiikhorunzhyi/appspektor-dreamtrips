package com.worldventures.dreamtrips.social.ui.feed.storage.command;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AccountTimelineStorageCommand extends FeedItemsStorageBaseCommand {

   public AccountTimelineStorageCommand(ListStorageOperation<FeedItem> operation) {
      super(operation);
   }
}
