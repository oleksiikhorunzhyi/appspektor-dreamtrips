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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DtlOfferDeserializer implements JsonDeserializer<DtlOffer> {

    private Gson gson;
    private Map<String, Type> modelByType = new HashMap<>();

    {
        modelByType.put(Offer.PERKS, new TypeToken<DtlOffer<DtlOfferPerkData>>() {
        }.getType());
        modelByType.put(Offer.POINT_REWARD, new TypeToken<DtlOffer<DtlOfferPointsData>>() {
        }.getType());
    }

    public DtlOfferDeserializer() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekDeserializer())
                .create();
    }

    @Override
    public DtlOffer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String type = json.getAsJsonObject().get("type").getAsString();

        if (modelByType.containsKey(type)) return gson.fromJson(json, modelByType.get(type));

        return null;
    }
}
