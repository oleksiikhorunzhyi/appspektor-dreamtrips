package com.worldventures.dreamtrips.modules.feed.model.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type.*;

public class FeedEntityDeserializer<T extends FeedEntityHolder> implements JsonDeserializer<T> {

    private Map<FeedEntityHolder.Type, Class<? extends FeedEntityHolder>> modelByType = new HashMap<>();

    {
        modelByType.put(TRIP, TripFeedItem.class);
        modelByType.put(POST, PostFeedItem.class);
        modelByType.put(PHOTO, PhotoFeedItem.class);
        modelByType.put(BUCKET_LIST_ITEM, BucketFeedItem.class);
        modelByType.put(UNDEFINED, UndefinedFeedItem.class);
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        FeedEntityHolder.Type type = null;
        JsonElement typeElement = json.getAsJsonObject().get("type");
        if (!typeElement.isJsonNull()) {
            type = context.deserialize(typeElement.getAsJsonPrimitive(), FeedEntityHolder.Type.class);
        }
        if (type == null) type = UNDEFINED;
        FeedEntityHolder model = context.deserialize(json, modelByType.get(type));
        return (T) model;
    }
}