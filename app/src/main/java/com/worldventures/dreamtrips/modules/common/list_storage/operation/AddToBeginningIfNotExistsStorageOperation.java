package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import java.util.List;

public class AddToBeginningIfNotExistsStorageOperation<T> implements ListStorageOperation<T> {

   private T item;

   public AddToBeginningIfNotExistsStorageOperation(T item) {
      this.item = item;
   }

   @Override
   public List<T> perform(List<T> items) {
      if (!items.isEmpty() && !items.get(0).equals(item)) {
         items.add(0, item);
      }
      return items;
   }
}
