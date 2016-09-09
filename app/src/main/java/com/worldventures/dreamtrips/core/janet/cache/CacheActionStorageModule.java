package com.worldventures.dreamtrips.core.janet.cache;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.RecentlyAddedBucketItemStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlMerchantsStorage;
import com.worldventures.dreamtrips.modules.dtl.helper.cache.DtlSearchLocationStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.TranslationDiscStorage;
import com.worldventures.dreamtrips.modules.flags.storage.FlagsStorage;
import com.worldventures.dreamtrips.modules.friends.service.CirclesStorage;
import com.worldventures.dreamtrips.modules.trips.service.storage.ActivitiesStorage;
import com.worldventures.dreamtrips.modules.trips.service.storage.RegionsStorage;

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
}
