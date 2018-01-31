package com.worldventures.dreamtrips.social.di;

import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.modules.infopages.service.storage.DocumentsDiskStorage;
import com.worldventures.core.modules.infopages.service.storage.DocumentsStorage;
import com.worldventures.core.modules.infopages.service.storage.FeedbackTypeActionStorage;
import com.worldventures.core.modules.infopages.service.storage.InfopagesStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValuePaginatedMemoryStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository;
import com.worldventures.dreamtrips.social.domain.storage.action.ContactsStorage;
import com.worldventures.dreamtrips.social.domain.storage.action.SuccessStoriesStorage;
import com.worldventures.dreamtrips.social.service.users.circle.storage.CirclesStorage;
import com.worldventures.dreamtrips.social.ui.background_uploading.storage.CompoundOperationRepository;
import com.worldventures.dreamtrips.social.ui.background_uploading.storage.CompoundOperationRepositoryImpl;
import com.worldventures.dreamtrips.social.ui.background_uploading.storage.CompoundOperationStorage;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.BucketListDiskStorage;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.BucketMemoryStorage;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.RecentlyAddedBucketItemStorage;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage;
import com.worldventures.dreamtrips.social.ui.feed.service.storage.ActiveFeedRouteStorage;
import com.worldventures.dreamtrips.social.ui.feed.service.storage.NotificationMemoryStorage;
import com.worldventures.dreamtrips.social.ui.feed.service.storage.NotificationsStorage;
import com.worldventures.dreamtrips.social.ui.feed.service.storage.PendingLikesStorage;
import com.worldventures.dreamtrips.social.ui.feed.service.storage.TranslationDiscStorage;
import com.worldventures.dreamtrips.social.ui.feed.storage.storage.AccountTimelineStorage;
import com.worldventures.dreamtrips.social.ui.feed.storage.storage.FeedStorage;
import com.worldventures.dreamtrips.social.ui.feed.storage.storage.HashtagFeedStorage;
import com.worldventures.dreamtrips.social.ui.feed.storage.storage.UserTimelineStorage;
import com.worldventures.dreamtrips.social.ui.flags.storage.FlagsStorage;
import com.worldventures.dreamtrips.social.ui.membership.storage.PodcastsDiskStorage;
import com.worldventures.dreamtrips.social.ui.membership.storage.PodcastsStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.InspireMeStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.TripImageStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.YsbhPhotoStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SocialActionStorageModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideBucketListStorage(SocialSnappyRepository snappyRepository) {
      return new BucketListDiskStorage(new BucketMemoryStorage(), snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTranslationStorage(SocialSnappyRepository snappyRepository) {
      return new TranslationDiscStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideNotificationStorage(SocialSnappyRepository snappyRepository) {
      return new NotificationsStorage(snappyRepository, new NotificationMemoryStorage());
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideUploadControllerStorage() {
      return new UploadBucketPhotoInMemoryStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideRecentlyAddedBucketsStorage() {
      return new RecentlyAddedBucketItemStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideCirclesStorage(SocialSnappyRepository db) {
      return new CirclesStorage(db);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFlagsStorage(FlagsStorage flagsStorage) {
      return flagsStorage;
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage providePodcastsStorage(SocialSnappyRepository snappyRepository) {
      return new PodcastsStorage(new PaginatedMemoryStorage<>(), new PodcastsDiskStorage(snappyRepository));
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   MultipleActionStorage provideCompoundOperationStorage(CompoundOperationRepository compoundOperationRepository) {
      return new CompoundOperationStorage(compoundOperationRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   MultipleActionStorage provideTripImageStorage() {
      return new TripImageStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideInspitationStorage() {
      return new InspireMeStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideYsbhStorage() {
      return new YsbhPhotoStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFeedbackStorage(InfopagesStorage storage) {
      return new FeedbackTypeActionStorage(storage);
   }

   @Singleton
   @Provides
   PendingLikesStorage provideLikesStorage() {
      return new PendingLikesStorage();
   }

   @Singleton
   @Provides
   CompoundOperationRepository provideCompoundOperationRepository(SocialSnappyRepository snappyRepository) {
      return new CompoundOperationRepositoryImpl(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDocumentsStorage(InfopagesStorage db) {
      return new DocumentsStorage(new KeyValuePaginatedMemoryStorage<>(), new DocumentsDiskStorage(db));
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFeedItemsStorage() {
      return new FeedStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTimelineStorage() {
      return new AccountTimelineStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideUserTimelineStorage() {
      return new UserTimelineStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideHashtagFeedStorage() {
      return new HashtagFeedStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActiveFeedRouteStorage provideActiveFeedRouteStorage() {
      return new ActiveFeedRouteStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   MultipleActionStorage provideContactStorage() {
      return new ContactsStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   MultipleActionStorage provideSuccessStoriesStorage() {
      return new SuccessStoriesStorage();
   }
}
