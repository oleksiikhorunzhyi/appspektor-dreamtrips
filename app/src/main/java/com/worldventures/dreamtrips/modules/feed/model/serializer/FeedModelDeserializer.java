package com.worldventures.dreamtrips.modules.feed.model.serializer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;

import java.lang.reflect.Type;
import java.util.Date;

public class FeedModelDeserializer implements JsonDeserializer<BaseEventModel> {

    Gson gson = new Gson();

    public FeedModelDeserializer() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .create();
    }

    @Override
    public BaseEventModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        BaseEventModel.Type type = gson.fromJson(json.getAsJsonObject().getAsJsonPrimitive("type"), BaseEventModel.Type.class);

        if (type != null) {
            return gson.fromJson(json, type.getClazz());
        } else {
            return new FeedUndefinedEventModel();
        }
    }
}