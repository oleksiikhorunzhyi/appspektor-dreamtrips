package com.worldventures.dreamtrips.core.janet.cache.storage;


import com.worldventures.dreamtrips.core.janet.cache.CachedAction;

import java.util.List;

public interface MultipleActionStorage<T> extends Storage<T> {
   List<Class<? extends CachedAction>> getActionClasses();
}
