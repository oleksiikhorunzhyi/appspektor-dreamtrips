package com.worldventures.dreamtrips.api.api_common.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.util.DateTimeUtils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

    private final DateFormat[] dateFormats;
    private final Object lock = new Object();

    public DateTimeDeserializer() {
        dateFormats = DateTimeUtils.getISO1DateFormats();
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String elementAsString = json.getAsString();
        if (elementAsString.isEmpty()) return null;

        List<Throwable> issues = new LinkedList<Throwable>();
        synchronized (lock) {
            for (DateFormat format : dateFormats) {
                try {
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = format.parse(elementAsString);
                    return date;
                } catch (Throwable e) {
                    issues.add(e);
                }
            }
        }
        throw new JsonParseException("Can't parse date '" + elementAsString + "'" + "\nIssues: " + issues);
    }
}
