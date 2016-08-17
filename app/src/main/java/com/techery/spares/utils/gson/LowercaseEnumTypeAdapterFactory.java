package com.techery.spares.utils.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory {
   private String fallbackKey;

   public LowercaseEnumTypeAdapterFactory(String fallbackKey) {
      this.fallbackKey = LowercaseEnumTypeAdapter.toLowercase(fallbackKey);
   }

   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? extends T> rawType = (Class<T>) type.getRawType();
      if (!rawType.isEnum()) {
         return null;
      }

      return (TypeAdapter<T>) new LowercaseEnumTypeAdapter(rawType, fallbackKey);
   }

   public static class LowercaseEnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {

      protected final Map<String, T> lowerToEnum = new HashMap<>();
      protected final Map<T, String> enumToLower = new HashMap<>();

      protected final String fallbackKey;

      public LowercaseEnumTypeAdapter(Class<T> classOfT, String fallbackKey) {
         this.fallbackKey = fallbackKey;

         try {
            for (T constant : classOfT.getEnumConstants()) {
               String name = constant.name();
               SerializedName annotation = classOfT.getField(name).getAnnotation(SerializedName.class);
               if (annotation != null) {
                  name = annotation.value();
                  for (String alternate : annotation.alternate()) {
                     lowerToEnum.put(alternate.toLowerCase(), constant);
                  }
               }
               lowerToEnum.put(name.toLowerCase(), constant);
               enumToLower.put(constant, name.toLowerCase());
            }
         } catch (NoSuchFieldException e) {
            throw new AssertionError("Missing field in " + classOfT.getName(), e);
         }
      }

      @Override
      public void write(JsonWriter out, T value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value(toLowercase(value));
         }
      }

      @Override
      public T read(JsonReader reader) throws IOException {
         if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
         } else {
            T t = lowerToEnum.get(reader.nextString().toLowerCase());
            return t != null ? t : lowerToEnum.get(fallbackKey);
         }
      }

      private static String toLowercase(Object o) {
         return o.toString().toLowerCase(Locale.US);
      }

   }
}
