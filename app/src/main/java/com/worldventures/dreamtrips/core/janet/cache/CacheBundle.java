package com.worldventures.dreamtrips.core.janet.cache;

public interface CacheBundle {
    <T> void put(String label, T params);

    <T> T get(String label, T empty);

    <T> T get(String label);

    boolean contains(String label);
}