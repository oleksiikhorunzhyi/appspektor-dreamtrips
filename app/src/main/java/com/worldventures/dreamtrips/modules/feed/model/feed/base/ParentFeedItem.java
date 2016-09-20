package com.worldventures.dreamtrips.modules.feed.model.feed.base;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class ParentFeedItem {

   private String type;
   List<FeedItem<FeedEntity>> items;

   public List<FeedItem<FeedEntity>> getItems() {
      return items;
   }

   public String getType() {
      return type;
   }

   public boolean isSingle() {
      return type.equals("Single");
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setItems(List<FeedItem<FeedEntity>> items) {
      this.items = items;
   }
}
