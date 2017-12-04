package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import java.util.List;

public class AddItemsStorageOperation<T> implements ListStorageOperation<T> {

   private final List<T> operationItems;

   public AddItemsStorageOperation(List<T> items) {
      this.operationItems = items;
   }

   @Override
   public List<T> perform(List<T> items) {
      items.addAll(operationItems);
      return items;
   }
}
