package com.worldventures.dreamtrips.common;

import com.worldventures.dreamtrips.janet.MockDaggerActionService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class Injector {

   private final Map<Class, ObjectProvider> injectionMap = new HashMap<>();

   public <T> Injector registerProvider(Class<T> objClass, ObjectProvider<T> provider) {
      if (injectionMap.containsKey(objClass)) {
         throw new IllegalArgumentException("Class " + objClass + " is already registered");
      }
      injectionMap.put(objClass, provider);
      return this;
   }

   public void inject(Object object) throws IllegalAccessException {
      injectInternal(object, object.getClass());
   }

   protected void injectInternal(Object object, Class<?> clazz) throws IllegalAccessException {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         if (field.getAnnotation(Inject.class) != null) {
            ObjectProvider provider = injectionMap.get(field.getType());
            if (provider != null) {
               field.setAccessible(true);
               field.set(object, provider.provide());
            }
         }
      }

      if (clazz.getSuperclass() != null) {
         injectInternal(object, clazz.getSuperclass());
      }
   }
}
