package com.techery.spares.storage.complex_objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techery.spares.storage.ObjectStorage;
import com.techery.spares.storage.preferences.ObjectPreferenceStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;

import java.lang.reflect.Type;
import java.util.Date;

public class ComplexObjectStorage<T> implements ObjectStorage<T> {

   private final Gson gson;
   private final ObjectStorage<String> storage;
   private final Class<T> typeClass;
   private final Type type;
   private Optional<T> cachedInstance = Optional.absent();

   {
      gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTimeDeserializer()).create();
   }

   public ComplexObjectStorage(SimpleKeyValueStorage storage, String key, Class<T> objectClass) {
      this(new ObjectPreferenceStorage(storage, key), objectClass);
   }

   public ComplexObjectStorage(ObjectStorage<String> storage, Class<T> objectClass) {
      this.storage = storage;
      this.typeClass = objectClass;
      this.type = null;
   }

   public ComplexObjectStorage(SimpleKeyValueStorage storage, String key, Type type) {
      this(new ObjectPreferenceStorage(storage, key), type);
   }

   public ComplexObjectStorage(ObjectStorage<String> storage, Type type) {
      this.storage = storage;
      this.typeClass = null;
      this.type = type;
   }

   public Optional<T> get() {
      if (!cachedInstance.isPresent()) {
         Optional<String> value = this.storage.get();

         if (value.isPresent()) {
            T item;
            if (typeClass != null) item = this.gson.fromJson(value.get(), typeClass);
            else if (type != null) item = this.gson.fromJson(value.get(), type);
            else throw new IllegalStateException("Neither type or typeClass is set to deserialize");
            cachedInstance = Optional.of(item);
         }
      }

      return cachedInstance;
   }

   public void put(T obj) {
      this.storage.put(this.gson.toJson(obj));
      this.cachedInstance = Optional.of(obj);
   }

   @Override
   public void destroy() {
      this.storage.destroy();
      this.cachedInstance = Optional.absent();
   }
}
