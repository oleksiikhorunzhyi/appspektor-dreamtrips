package com.worldventures.janet.cache.storage;

@SuppressWarnings("InterfaceIsType")
public interface PaginatedStorage<T> extends Storage<T> {

   String BUNDLE_REFRESH = "REFRESH";
}
