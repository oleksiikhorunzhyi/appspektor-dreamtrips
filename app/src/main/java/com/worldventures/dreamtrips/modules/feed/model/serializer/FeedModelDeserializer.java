package com.worldventures.dreamtrips.modules.feed.model.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;

import java.lang.reflect.Type;

public class FeedModelDeserializer implements JsonDeserializer<BaseFeedModel> {

    Gson gson = new Gson();

    public FeedModelDeserializer() {
    }

    @Override
    public BaseFeedModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        BaseFeedModel.Type type = gson.fromJson(json.getAsJsonObject().getAsJsonObject("type"), BaseFeedModel.Type.class);

        return gson.fromJson(json,type.getClazz());
    }
}
