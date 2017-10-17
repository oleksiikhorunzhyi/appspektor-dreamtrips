package com.worldventures.dreamtrips.social.ui.feed.model.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.UndefinedFeedItem;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type.BUCKET_LIST_ITEM;
import static com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type.PHOTO;
import static com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type.POST;
import static com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type.TRIP;
import static com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder.Type.UNDEFINED;

public class FeedEntityDeserializer<T extends FeedEntityHolder> implements JsonDeserializer<T> {

   private static final Map<FeedEntityHolder.Type, Class<? extends FeedEntityHolder>> modelByType = new HashMap<>();

   static {
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
      if (type == null) {
         type = UNDEFINED;
      }
      return (T) context.deserialize(json, modelByType.get(type));
   }
}
