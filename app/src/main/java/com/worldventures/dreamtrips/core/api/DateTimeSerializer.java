package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.worldventures.dreamtrips.modules.tripsimages.model.DateTime;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeSerializer implements JsonSerializer<DateTime> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(dateFormat.format(src));
    }
}
