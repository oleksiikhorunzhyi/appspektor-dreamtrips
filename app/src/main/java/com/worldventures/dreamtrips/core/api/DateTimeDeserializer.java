package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

    private DateFormat[] dateFormats;

    public DateTimeDeserializer() {
        dateFormats = DateTimeUtils.getISO1DateFormats();
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        for (DateFormat format : dateFormats) {
            try {
                Date date = format.parse(json.getAsString());
                date = fixTimeZone(date);
                return date;
            } catch (ParseException e) {
            }
        }
        Timber.e("Can't parse date with any format, date string: %s", json);
        return null;
    }

    private Date fixTimeZone(Date date) {
        DateTime dateTime = new DateTime(date);
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        return dateTime.withZoneRetainFields(DateTimeZone.UTC).withZone(dateTimeZone).toDate();
    }
}
