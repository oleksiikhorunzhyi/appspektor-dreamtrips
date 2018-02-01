package com.worldventures.janet.cache.storage;

import com.worldventures.janet.cache.CachedAction;

public interface ActionStorage<T> extends Storage<T> {
   Class<? extends CachedAction> getActionClass();
}