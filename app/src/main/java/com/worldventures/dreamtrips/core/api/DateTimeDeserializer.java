package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

public class DateTimeDeserializer implements JsonDeserializer<Date> {


    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        for (DateFormat format : DateTimeUtils.getISO1DateFormats()) {
            try {
                return format.parse(json.getAsString());
            } catch (ParseException e) {
                Timber.e(e, "Can't parse");
            }
        }
        return null;
    }
}
