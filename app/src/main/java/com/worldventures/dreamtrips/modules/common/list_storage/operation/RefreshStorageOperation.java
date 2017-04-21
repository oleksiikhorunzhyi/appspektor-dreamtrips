package com.worldventures.dreamtrips.modules.common.list_storage.operation;


import java.util.List;

public class RefreshStorageOperation<T> implements ListStorageOperation<T> {

   private List<T> operationItems;

   public RefreshStorageOperation(List<T> items) {
      this.operationItems = items;
   }

   @Override
   public List<T> perform(List<T> items) {
      items.clear();
      items.addAll(operationItems);
      return items;
   }
}
