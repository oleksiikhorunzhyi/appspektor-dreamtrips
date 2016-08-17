package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;

import java.lang.reflect.Type;

public class DayOfWeekDeserializer implements JsonDeserializer<DayOfWeek> {

   @Override
   public DayOfWeek deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return DayOfWeek.from(json.getAsString());
   }
}
