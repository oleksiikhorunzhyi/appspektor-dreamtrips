package com.worldventures.dreamtrips.core.janet.cache;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.RecentlyAddedBucketItemStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlLocationStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlMerchantsStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlSearchLocationStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationMemoryStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationsStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.TranslationDiscStorage;
import com.worldventures.dreamtrips.modules.flags.storage.FlagsStorage;
import com.worldventures.dreamtrips.modules.friends.service.CirclesStorage;
import com.worldventures.dreamtrips.modules.infopages.service.storage.FeedbackTypeStorage;
import com.worldventures.dreamtrips.modules.membership.storage.PodcastsDiskStorage;
import com.worldventures.dreamtrips.modules.membership.storage.PodcastsStorage;
import com.worldventures.dreamtrips.modules.trips.service.storage.ActivitiesStorage;
import com.worldventures.dreamtrips.modules.trips.service.storage.RegionsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripDetailsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripPinsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsByUidsStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsDiskStorage;
import com.worldventures.dreamtrips.modules.trips.storage.TripsStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class CacheActionStorageModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDtlMerchantsStorage(SnappyRepository db) {
      return new DtlMerchantsStorage(db);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDtlSearchLocationStorage() {
      return new DtlSearchLocationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDtlLocationStorage(SnappyRepository db) {
      return new DtlLocationStorage(db);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideBucketListStorage(SnappyRepository snappyRepository) {
      return new BucketListDiskStorage(new BucketMemoryStorage(), snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTranslationStorage(SnappyRepository snappyRepository) {
      return new TranslationDiscStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideNotificationStorage(SnappyRepository snappyRepository) {
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
   ActionStorage provideActivitiesStorage() {
      return new ActivitiesStorage(new MemoryStorage<>());
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideRegionsStorage() {
      return new RegionsStorage(new MemoryStorage<>());
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideCirclesStorage(SnappyRepository db) {
      return new CirclesStorage(db);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFlagsStorage(FlagsStorage flagsStorage) {
      return flagsStorage;
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage providePodcastsStorage(SnappyRepository snappyRepository) {
      return new PodcastsStorage(new PaginatedMemoryStorage<>(), new PodcastsDiskStorage(snappyRepository));
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsStorage(SnappyRepository snappyRepository) {
      return new TripsStorage(new PaginatedMemoryStorage<>(), new TripsDiskStorage(snappyRepository));
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsPinsStorage(SnappyRepository snappyRepository) {
      return new TripPinsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripsDetailsStorage(SnappyRepository snappyRepository) {
      return new TripsByUidsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTripDetailsStorage(SnappyRepository snappyRepository) {
      return new TripDetailsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFeedbackStorage(SnappyRepository db) {
      return new FeedbackTypeStorage(db);
   }
}
