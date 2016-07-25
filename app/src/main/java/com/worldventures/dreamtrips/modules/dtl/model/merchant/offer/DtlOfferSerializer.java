package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DtlOfferSerializer implements JsonSerializer<DtlOffer> {

    @Override
    public JsonElement serialize(DtlOffer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = context.serialize(src);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.addProperty("type", src.getType().value());
        return jsonObject;
    }
}
