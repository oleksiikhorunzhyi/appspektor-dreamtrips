package com.worldventures.dreamtrips.core.janet.cache;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CacheOptions {
    @Value.Default
    public boolean restoreFromCache() {
        return true;
    }

    @Value.Default
    public boolean saveToCache() {
        return true;
    }

    @Value.Default
    public boolean sendAfterRestore() {
        return true;
    }
}
