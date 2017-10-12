package com.worldventures.core.janet.cache.storage;

import com.worldventures.core.janet.cache.CachedAction;

public interface ActionStorage<T> extends Storage<T> {
   Class<? extends CachedAction> getActionClass();
}