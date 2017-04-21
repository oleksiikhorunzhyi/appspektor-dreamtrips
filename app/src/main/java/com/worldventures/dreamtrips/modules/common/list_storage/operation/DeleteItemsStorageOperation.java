package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import java.util.List;

public class DeleteItemsStorageOperation<T> implements ListStorageOperation<T> {

   private T item;

   public DeleteItemsStorageOperation(T item) {
      this.item = item;
   }

   @Override
   public List<T> perform(List<T> items) {
      int index = items.indexOf(item);
      if (index != -1) {
         items.remove(index);
      }
      return items;
   }
}
