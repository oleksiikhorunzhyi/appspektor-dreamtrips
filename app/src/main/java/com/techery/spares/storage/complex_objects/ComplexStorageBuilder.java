package com.techery.spares.storage.complex_objects;

import com.techery.spares.storage.ObjectStorage;
import com.techery.spares.storage.preferences.ObjectPreferenceStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;


public class ComplexStorageBuilder {
   private final SimpleKeyValueStorage simpleKeyValueStorage;

   public ComplexStorageBuilder(SimpleKeyValueStorage simpleKeyValueStorage) {
      this.simpleKeyValueStorage = simpleKeyValueStorage;
   }

   public final <T> ObjectStorage<T> build(final String key, final Class<T> objectClass) {
      return new ComplexObjectStorage<T>(getStorage(key), objectClass);
   }

   private ObjectPreferenceStorage getStorage(final String key) {
      return new ObjectPreferenceStorage(simpleKeyValueStorage, key);
   }
}
