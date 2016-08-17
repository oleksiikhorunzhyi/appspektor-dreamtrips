package com.worldventures.dreamtrips.core.janet.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CacheBundleImpl implements CacheBundle {
   private Map<String, Object> internalParams = new ConcurrentHashMap<>();

   public <T> void put(String label, T param) {
      internalParams.put(label, param);
   }

   public <T> T get(String label, T defaultParam) {
      Object param = internalParams.get(label);
      return param == null ? defaultParam : (T) param;
   }

   @Override
   public <T> T get(String label) {
      return get(label, null);
   }

   @Override
   public boolean contains(String label) {
      return internalParams.containsKey(label);
   }
}