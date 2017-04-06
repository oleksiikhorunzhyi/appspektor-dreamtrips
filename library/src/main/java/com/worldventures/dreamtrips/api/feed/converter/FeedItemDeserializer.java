package com.worldventures.dreamtrips.api.feed.converter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.entity.converter.EntityDeserializer;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;

import org.immutables.gson.Gson.TypeAdapters;
import org.immutables.value.Value;

import java.lang.reflect.Type;

@TypeAdapters
public class FeedItemDeserializer implements JsonDeserializer<FeedItem> {

    private Gson gson;
    private EntityDeserializer entityDeserializer;

    public FeedItemDeserializer(Gson gson, EntityDeserializer entityDeserializer) {
        this.gson = gson;
        this.entityDeserializer = entityDeserializer;
    }

    @Override
    public FeedItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        EntityHolder<? extends UniqueIdentifiable> entityHolder = entityDeserializer.deserialize(json, typeOfT, context);
        JsonFeedItem jsonFeedItem = gson.fromJson(json, JsonFeedItem.class);
        return ImmutableObjFeedItem.builder()
                .id(jsonFeedItem.id())
                .type(entityHolder.type())
                .entity(entityHolder.entity())
                .action(jsonFeedItem.action())
                .links(jsonFeedItem.links())
                .readAt(jsonFeedItem.readAt())
                .createdAt(jsonFeedItem.createdAt())
                .build();
    }

    @TypeAdapters
    @Value.Immutable
    interface ObjFeedItem<T extends UniqueIdentifiable> extends FeedItem<T> {}

    @TypeAdapters
    @Value.Immutable
    interface JsonFeedItem extends FeedItem<EntityDeserializer.UnknownUniqueIdentifiable> {}

}
