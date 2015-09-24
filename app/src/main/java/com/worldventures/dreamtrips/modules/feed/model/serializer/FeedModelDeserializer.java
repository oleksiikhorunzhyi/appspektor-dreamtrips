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
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.worldventures.dreamtrips.modules.feed.model.BaseEventModel.Type.UNDEFINED;

public class FeedModelDeserializer implements JsonDeserializer<BaseEventModel> {

    private Gson gson;
    private Map<BaseEventModel.Type, Class<? extends BaseEventModel>> modelByType = new HashMap<>();

    {
        modelByType.put(BaseEventModel.Type.TRIP, FeedTripEventModel.class);
        modelByType.put(BaseEventModel.Type.POST, FeedPostEventModel.class);
        modelByType.put(BaseEventModel.Type.PHOTO, FeedPhotoEventModel.class);
        modelByType.put(BaseEventModel.Type.BUCKET_LIST_ITEM, FeedBucketEventModel.class);
        modelByType.put(UNDEFINED, FeedUndefinedEventModel.class);
    }

    public FeedModelDeserializer() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .create();
    }

    @Override
    public BaseEventModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BaseEventModel.Type type = null;
        JsonElement typeElement = json.getAsJsonObject().get("type");
        if (!typeElement.isJsonNull()) {
            type = gson.fromJson(typeElement.getAsJsonPrimitive(), BaseEventModel.Type.class);
        }
        if (type == null) type = UNDEFINED;
        BaseEventModel model = gson.fromJson(json, modelByType.get(type));
        if (model.getType() == null) model.setType(UNDEFINED);
        return model;
    }
}