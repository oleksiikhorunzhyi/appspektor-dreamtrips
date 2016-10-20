package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.photos.model.Photo;
import com.worldventures.dreamtrips.api.post.model.response.Post;
import com.worldventures.dreamtrips.api.trip.model.Trip;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import io.techery.mappery.MapperyContext;

public class FeedItemConverter implements Converter<FeedItem, com.worldventures.dreamtrips.modules.feed.model.FeedItem> {

   @Override
   public Class<FeedItem> sourceClass() {
      return FeedItem.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.modules.feed.model.FeedItem> targetClass() {
      return com.worldventures.dreamtrips.modules.feed.model.FeedItem.class;
   }

   @Override
   public com.worldventures.dreamtrips.modules.feed.model.FeedItem convert(MapperyContext mapperyContext, FeedItem apiFeedItem) {
      com.worldventures.dreamtrips.modules.feed.model.FeedItem feedItem = new com.worldventures.dreamtrips.modules.feed.model.FeedItem();
      feedItem.setCreatedAt(apiFeedItem.createdAt());
      feedItem.setLinks(mapperyContext.convert(apiFeedItem.links(), Links.class));
      feedItem.setAction(mapAction(apiFeedItem.action()));
      TargetClassInfo targetClassInfo = getTargetClassInfo(apiFeedItem.entity());
      try {
         feedItem.setType(targetClassInfo.type);
         feedItem.setItem(mapperyContext.convert(apiFeedItem.entity(), targetClassInfo.targetClass));
      } catch (IllegalArgumentException ex) {
         return new UndefinedFeedItem();
      }
      return feedItem;
   }

   private TargetClassInfo getTargetClassInfo(Object entity) {
      if (entity instanceof Trip) {
         return new TargetClassInfo(FeedEntityHolder.Type.TRIP, TripModel.class);
      }
      if (entity instanceof Post) {
         return new TargetClassInfo(FeedEntityHolder.Type.POST, TextualPost.class);
      }
      if (entity instanceof Photo) {
         return new TargetClassInfo(FeedEntityHolder.Type.PHOTO,
               com.worldventures.dreamtrips.modules.tripsimages.model.Photo.class);
      }
      if (entity instanceof BucketItem) {
         return new TargetClassInfo(FeedEntityHolder.Type.BUCKET_LIST_ITEM,
               com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.class);
      }
      throw new IllegalArgumentException("No such class found for the feed");
   }

   private com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action mapAction(FeedItem.Action action) {
      switch (action) {
         case ADD:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.ADD;
         case BOOK:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.BOOK;
         case COMMENT:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.COMMENT;
         case LIKE:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.LIKE;
         case REJECT_REQUEST:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.REJECT_REQUEST;
         case ACCEPT_REQUEST:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.ACCEPT_REQUEST;
         case SEND_REQUEST:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.SEND_REQUEST;
         case SHARE:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.SHARE;
         case TAG_PHOTO:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.TAG_PHOTO;
         case UPDATE:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.UPDATE;
         case UNKNOWN:
            return com.worldventures.dreamtrips.modules.feed.model.FeedItem.Action.UNKNOWN;
      }
      throw new IllegalArgumentException("No such type");
   }

   private static class TargetClassInfo {
      private FeedEntityHolder.Type type;
      private Class<? extends FeedEntity> targetClass;

      public TargetClassInfo(FeedEntityHolder.Type type, Class<? extends FeedEntity> targetClass) {
         this.type = type;
         this.targetClass = targetClass;
      }
   }
}
