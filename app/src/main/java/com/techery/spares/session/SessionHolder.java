package com.techery.spares.session;

import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

public class SessionHolder<S> extends ComplexObjectStorage<S> {

   public interface Events {
      class SessionDestroyed {}
   }

   public SessionHolder(SimpleKeyValueStorage keyValueStorage, Class<S> cls) {
      super(keyValueStorage, "SESSION_KEY", cls);
   }

}
