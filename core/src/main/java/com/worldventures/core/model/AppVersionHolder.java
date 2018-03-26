package com.worldventures.core.model;

import com.worldventures.core.storage.complex_objects.ComplexObjectStorage;
import com.worldventures.core.storage.preferences.SimpleKeyValueStorage;

public class AppVersionHolder extends ComplexObjectStorage<Integer> {

   public AppVersionHolder(SimpleKeyValueStorage storage) {
      super(storage, "APP_VERSION", Integer.class);
   }
}
