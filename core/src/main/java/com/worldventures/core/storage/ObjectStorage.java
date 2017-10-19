package com.worldventures.core.storage;


import com.worldventures.core.storage.complex_objects.Optional;

public interface ObjectStorage<T> {
   Optional<T> get();

   void put(T obj);

   void destroy();
}
