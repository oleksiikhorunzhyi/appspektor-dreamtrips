package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.worldventures.dreamtrips.modules.tripsimages.model.DateTime;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeDeserializer implements JsonDeserializer<DateTime> {

    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            return new DateTime(df.parse(json.getAsString()));
        } catch (ParseException e) {
            return null;
        }
    }

}
