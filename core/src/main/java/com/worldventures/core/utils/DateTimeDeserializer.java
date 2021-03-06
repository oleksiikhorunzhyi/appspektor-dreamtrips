package com.worldventures.core.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

   private final DateFormat[] dateFormats;

   public DateTimeDeserializer() {
      dateFormats = DateTimeUtils.getISO1DateFormats();
   }

   @Override
   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      for (DateFormat format : dateFormats) {
         try {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(json.getAsString());
         } catch (ParseException ignored) {
         }
      }
      Timber.e("Can't parse date with any format, date string: %s", json);
      return null;
   }

}
