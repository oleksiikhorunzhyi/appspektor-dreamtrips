package com.worldventures.core.storage.preferences;

import com.worldventures.core.storage.ObjectStorage;
import com.worldventures.core.storage.complex_objects.Optional;

public class ObjectPreferenceStorage implements ObjectStorage<String> {
   private final SimpleKeyValueStorage simpleKeyValueStorage;
   private final String key;

   public ObjectPreferenceStorage(SimpleKeyValueStorage simpleKeyValueStorage, String key) {
      this.simpleKeyValueStorage = simpleKeyValueStorage;
      this.key = key;
   }

   @Override
   public Optional<String> get() {
      return Optional.fromNullable(this.simpleKeyValueStorage.get(key));
   }

   @Override
   public void put(String obj) {
      this.simpleKeyValueStorage.put(key, obj);
   }

   @Override
   public void destroy() {
      this.simpleKeyValueStorage.remove(key);
   }
}
