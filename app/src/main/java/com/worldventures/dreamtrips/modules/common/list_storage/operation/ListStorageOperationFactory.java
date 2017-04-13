package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.List;

public class ListStorageOperationFactory {

   public static ListStorageOperation refreshItemsOperation(List<FeedItem> items) {
      return new RefreshStorageOperation(items);
   }

   public static ListStorageOperation addItemsOperation(List<FeedItem> items) {
      return new AddToStorageOperation(items);
   }

   public static ListStorageOperation addItemToBeginningOperation(FeedItem item) {
      return new AddToBeginningStorageOperation(item);
   }

   public static ListStorageOperation updateItemOperation(FeedItem feedItem) {
      return new UpdateItemsStorageOperation(feedItem);
   }

   public static ListStorageOperation deleteItemOperation(FeedItem feedItem) {
      return new DeleteItemsStorageOperation(feedItem);
   }
}
