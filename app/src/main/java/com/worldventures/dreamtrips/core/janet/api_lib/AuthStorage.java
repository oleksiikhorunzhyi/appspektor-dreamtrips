package com.worldventures.dreamtrips.core.janet.api_lib;

public abstract class AuthStorage<T> {

   private final Class<T> clazz;

   public AuthStorage(Class<T> clazz) {
      this.clazz = clazz;
   }

   public Class<T> getAuthType() {
      return clazz;
   }

   public abstract void storeAuth(T auth);
}

