package com.worldventures.dreamtrips.modules.common.list_storage.operation;

import java.util.List;

public interface ListStorageOperation<T> {
   List<T> perform(List<T> items);
}

