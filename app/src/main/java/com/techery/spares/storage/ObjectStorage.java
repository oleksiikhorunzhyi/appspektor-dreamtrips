package com.techery.spares.storage;


import com.techery.spares.storage.complex_objects.Optional;

public interface ObjectStorage<T> {
   Optional<T> get();

   void put(T obj);

   void destroy();
}
