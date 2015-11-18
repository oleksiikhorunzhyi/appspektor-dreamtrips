package com.worldventures.dreamtrips.modules.dtl.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public enum DtlLocationCategory {
    CITY,
    METRO,
    STATE,
    COUNTRY;

    public final static TypeAdapter<DtlLocationCategory> adapter = new DtlLocationCategoryAdapter();

    public static class DtlLocationCategoryAdapter extends TypeAdapter<DtlLocationCategory> {

        @Override
        public void write(JsonWriter out, DtlLocationCategory value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.name());
            }
        }

        @Override
        public DtlLocationCategory read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else {
                DtlLocationCategory t = valueOf(reader.nextString());
                return t != null ? t : CITY;
            }
        }
    }
}
