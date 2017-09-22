package com.worldventures.dreamtrips.social.domain.storage;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.friends.model.Circle;
import com.worldventures.dreamtrips.social.ui.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLanguage;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLocale;

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

   void saveLastSelectedVideoLocale(VideoLocale videoLocale);

   VideoLocale getLastSelectedVideoLocale();

   void saveLastSelectedVideoLanguage(VideoLanguage videoLocale);

   VideoLanguage getLastSelectedVideoLanguage();

   void saveTranslation(String originalText, String translation, String toLanguage);

   String getTranslation(String originalText, String toLanguage);

   void saveCircles(List<Circle> circles);

   List<Circle> getCircles();

   void saveFilterCircle(Circle circle);

   Circle getFilterCircle();

   Circle getFeedFriendPickedCircle();

   void saveFeedFriendPickedCircle(Circle circle);

   List<FeedbackType> getFeedbackTypes();

   void setFeedbackTypes(List<FeedbackType> types);

   void saveNotifications(List<FeedItem> notifications);

   List<FeedItem> getNotifications();

   void savePodcasts(List<Podcast> podcasts);

   List<Podcast> getPodcasts();
}
