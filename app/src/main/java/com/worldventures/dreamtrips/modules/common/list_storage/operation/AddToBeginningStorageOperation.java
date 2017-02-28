package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import java.util.List;

public class AddToBeginningStorageOperation<T> implements ListStorageOperation<T> {

   private T item;

   public AddToBeginningStorageOperation(T item) {
      this.item = item;
   }

   @Override
   public List<T> perform(List<T> items) {
      items.add(0, item);
      return items;
   }
}
