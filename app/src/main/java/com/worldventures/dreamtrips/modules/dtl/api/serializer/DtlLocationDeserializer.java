package com.worldventures.dreamtrips.modules.dtl.api.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.lang.reflect.Type;
import java.util.Date;

public class DtlLocationDeserializer implements JsonDeserializer<DtlLocation> {
    private Gson gson;

    public DtlLocationDeserializer() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .create();
    }

    @Override
    public DtlLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DtlLocation dtlLocation = gson.fromJson(json, typeOfT);
        if (dtlLocation.getCoordinates() == null) dtlLocation.setCoordinates(new Location(0.0d, 0.0d));
        return dtlLocation;
    }
}
