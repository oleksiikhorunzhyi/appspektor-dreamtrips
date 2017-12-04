package com.worldventures.dreamtrips.social.service.invites.operation;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;

import java.util.List;

public class EmptyOperation<T> implements ListStorageOperation<T> {
   @Override
   public List<T> perform(List<T> items) {
      return items;
   }
}
