package com.worldventures.dreamtrips.modules.feed.model.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.lang.reflect.Type;

import static com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type.UNDEFINED;


public class FeedItemDeserializer extends FeedEntityDeserializer<FeedItem> {

   @Override
   public FeedItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      FeedItem item = super.deserialize(json, typeOfT, context);
      if (item.getType() == null) item.setType(UNDEFINED);
      return item;
   }
}