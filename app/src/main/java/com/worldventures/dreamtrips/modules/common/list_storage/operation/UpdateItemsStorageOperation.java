package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import java.util.List;

public class UpdateItemsStorageOperation<T> implements ListStorageOperation<T> {

   private T item;

   public UpdateItemsStorageOperation(T item) {
      this.item = item;
   }

   @Override
   public List<T> perform(List<T> items) {
      int index = items.indexOf(item);
      if (index != -1) {
         items.set(index, item);
      }
      return items;
   }
}
