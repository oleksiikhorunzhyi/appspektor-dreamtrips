package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DtlOfferDeserializer implements JsonDeserializer<DtlOffer> {

    private final Gson gson;
    private static Map<String, java.lang.reflect.Type> modelByType = new HashMap<>();

    static {
        modelByType.put(DtlOffer.Type.PERK.value(), new TypeToken<DtlOfferPerk>() {}.getType());
        modelByType.put(DtlOffer.Type.POINTS.value(), new TypeToken<DtlOfferPoints>() {}.getType());
    }

    public DtlOfferDeserializer(final Gson gson) {
        this.gson = gson;
    }

    public DtlOfferDeserializer() {
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekDeserializer())
                .create();
    }

    @Override
    public DtlOffer deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        String type = json.getAsJsonObject().get("type").getAsString();
        JsonElement offerJson = json.getAsJsonObject().get("offer");

        return modelByType.containsKey(type) && !offerJson.isJsonNull() ? gson.fromJson(offerJson, modelByType.get(type)) : null;
    }
}
