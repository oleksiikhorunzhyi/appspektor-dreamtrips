package com.worldventures.dreamtrips.social.domain.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.model.Circle;
import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialSnappyRepositoryImpl extends BaseSnappyRepository implements SocialSnappyRepository {

   private static final String BUCKET_LIST = "bucket_items";
   private static final String OPEN_BUCKET_TAB_TYPE = "open_bucket_tab_type";
   private static final String SUGGESTED_PHOTOS_SYNC_TIME = "SUGGESTED_PHOTOS_SYNC_TIME";
   private static final String TRANSLATION = "translation";
   private static final String CIRCLES = "circles";
   private static final String FILTER_CIRCLE = "FILTER_CIRCLE";
   private static final String FILTER_FEED_FRIEND_FILTER_CIRCLE = "FILTER_FEED_FRIEND_FILTER_CIRCLE";
   private static final String PODCASTS = "PODCASTS";
   private static final String CATEGORIES = "categories";

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   public SocialSnappyRepositoryImpl(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Nullable
   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }

   @Override
   public <T> void putList(String key, Collection<T> list) {
      super.putList(key, list);
   }

   @Override
   public <T> List<T> readList(String key, Class<T> clazz) {
      return super.readList(key, clazz);
   }

   ///////////////////////////////////////////////////////////////////////////
   // BucketItems
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveBucketList(List<BucketItem> items, int userId) {
      putList(BUCKET_LIST + "_" + userId, items);
   }

   @Override
   public List<BucketItem> readBucketList(int userId) {
      return readBucketList(BUCKET_LIST + "_" + userId);
   }

   private List<BucketItem> readBucketList(String key) {
      List<BucketItem> list = readList(key, BucketItem.class);
      Collections.sort(list, (lhs, rhs) -> {
         if (lhs.isDone() == rhs.isDone()) return 0;
         else if (lhs.isDone() && !rhs.isDone()) return 1;
         else return -1;
      });
      return list;
   }

   @Override
   public void saveOpenBucketTabType(String type) {
      act(db -> db.put(OPEN_BUCKET_TAB_TYPE, type));
   }

   @Override
   public String getOpenBucketTabType() {
      return actWithResult(db -> db.get(OPEN_BUCKET_TAB_TYPE)).orNull();
   }

   @Override
   public void saveBucketListCategories(List<CategoryItem> categories) {
      putList(CATEGORIES, categories);
   }

   @Override
   public List<CategoryItem> getBucketListCategories() {
      return readList(CATEGORIES, CategoryItem.class);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Suggested photos
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveLastSuggestedPhotosSyncTime(long time) {
      act(db -> db.putLong(SUGGESTED_PHOTOS_SYNC_TIME, time));
   }

   @Override
   public long getLastSuggestedPhotosSyncTime() {
      return actWithResult(db -> db.getLong(SUGGESTED_PHOTOS_SYNC_TIME)).or(0L);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Rep Tools
   ///////////////////////////////////////////////////////////////////////////


   ///////////////////////////////////////////////////////////////////////////
   // Cached translations
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveTranslation(String originalText, String translation, String toLanguage) {
      act(db -> db.put(TRANSLATION + originalText + toLanguage, translation));
   }

   @Override
   public String getTranslation(String originalText, String toLanguage) {
      return actWithResult(db -> db.get(TRANSLATION + originalText + toLanguage)).or("");
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveCircles(List<Circle> circles) {
      if (circles == null) circles = new ArrayList<>();
      putList(CIRCLES, circles);
   }

   @Override
   public List<Circle> getCircles() {
      return readList(CIRCLES, Circle.class);
   }

   @Override
   public void saveFilterCircle(Circle circle) {
      act(db -> db.put(FILTER_CIRCLE, circle));
   }

   @Override
   public Circle getFilterCircle() {
      return actWithResult(db -> db.get(FILTER_CIRCLE, Circle.class)).orNull();
   }

   @Override
   public Circle getFeedFriendPickedCircle() {
      return actWithResult(db -> db.get(FILTER_FEED_FRIEND_FILTER_CIRCLE, Circle.class)).orNull();
   }

   @Override
   public void saveFeedFriendPickedCircle(Circle circle) {
      act(db -> db.put(FILTER_FEED_FRIEND_FILTER_CIRCLE, circle));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Notifications
   ///////////////////////////////////////////////////////////////////////////

   private static final String NOTIFICATIONS = "notifications";
   private static final String UNDEFINED_FEED_ITEM = "undefined";
   private static final String PHOTO_FEED_ITEM = "photo";
   private static final String POST_FEED_ITEM = "post";
   private static final String TRIP_FEED_ITEM = "trip";
   private static final String BUCKET_FEED_ITEM = "bucket";

   private static final Map<String, Class<? extends FeedItem>> feedItemsMapping = new HashMap<>();

   static {
      feedItemsMapping.put(NOTIFICATIONS + UNDEFINED_FEED_ITEM, UndefinedFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + TRIP_FEED_ITEM, TripFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + PHOTO_FEED_ITEM, PhotoFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + BUCKET_FEED_ITEM, BucketFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + POST_FEED_ITEM, PostFeedItem.class);
   }

   @Override
   public void saveNotifications(List<FeedItem> notifications) {
      for (Map.Entry<String, Class<? extends FeedItem>> entry : feedItemsMapping.entrySet()) {
         saveNotificationByType(notifications, entry.getValue(), entry.getKey());
      }
   }

   private void saveNotificationByType(List<FeedItem> notifications, Class itemClass, String typeKey) {
      List<FeedItem> notificationsByClass = Queryable.from(notifications)
            .filter(item -> item.getClass().equals(itemClass))
            .toList();
      putList(typeKey, notificationsByClass);
   }

   @Override
   public List<FeedItem> getNotifications() {
      List<FeedItem> feedItems = new ArrayList<>();
      for (Map.Entry<String, Class<? extends FeedItem>> entry : feedItemsMapping.entrySet()) {
         feedItems.addAll(readList(entry.getKey(), entry.getValue()));
      }
      return Queryable.from(feedItems)
            .sort((feedItemL, feedItemR) -> feedItemR.getCreatedAt().compareTo(feedItemL.getCreatedAt()))
            .toList();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Podcasts
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void savePodcasts(List<Podcast> podcasts) {
      if (podcasts == null) podcasts = new ArrayList<>();
      putList(PODCASTS, podcasts);
   }

   @Override
   public List<Podcast> getPodcasts() {
      return readList(PODCASTS, Podcast.class);
   }

}
