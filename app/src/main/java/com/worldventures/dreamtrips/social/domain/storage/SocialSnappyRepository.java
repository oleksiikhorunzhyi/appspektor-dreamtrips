package com.worldventures.dreamtrips.social.domain.storage;

import com.worldventures.core.model.Circle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;

import java.util.Collection;
import java.util.List;

public interface SocialSnappyRepository {

   <T> void putList(String key, Collection<T> list);

   <T> List<T> readList(String key, Class<T> clazz);

   void saveBucketList(List<BucketItem> items, int userId);

   List<BucketItem> readBucketList(int userId);

   void saveOpenBucketTabType(String type);

   String getOpenBucketTabType();

   void saveBucketListCategories(List<CategoryItem> categories);

   List<CategoryItem> getBucketListCategories();

   void saveLastSuggestedPhotosSyncTime(long time);

   long getLastSuggestedPhotosSyncTime();

   void saveTranslation(String originalText, String translation, String toLanguage);

   String getTranslation(String originalText, String toLanguage);

   void saveCircles(List<Circle> circles);

   List<Circle> getCircles();

   void saveFilterCircle(Circle circle);

   Circle getFilterCircle();

   Circle getFeedFriendPickedCircle();

   void saveFeedFriendPickedCircle(Circle circle);

   void saveNotifications(List<FeedItem> notifications);

   List<FeedItem> getNotifications();

   void savePodcasts(List<Podcast> podcasts);

   List<Podcast> getPodcasts();
}
