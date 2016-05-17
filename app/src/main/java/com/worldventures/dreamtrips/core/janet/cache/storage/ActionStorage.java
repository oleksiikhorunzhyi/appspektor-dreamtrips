package com.worldventures.dreamtrips.core.janet.cache.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;

public interface ActionStorage<T> extends Storage<T> {

    Class<? extends CachedAction> getActionClass();
}
