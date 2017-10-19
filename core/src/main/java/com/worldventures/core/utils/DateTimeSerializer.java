package com.worldventures.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

public class DateTimeSerializer implements JsonSerializer<Date> {

   @Override
   public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(DateTimeUtils.convertDateToUTCString(src));
   }
}
