package com.worldventures.dreamtrips.core.janet.cache.storage;


import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.Storage;

import java.util.List;

public interface MultipleActionStorage<T> extends Storage<T> {
   List<Class<? extends CachedAction>> getActionClasses();
}
