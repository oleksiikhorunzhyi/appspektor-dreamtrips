package com.worldventures.dreamtrips.core.janet.cache;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.background_uploading.storage.CompoundOperationRepository;
import com.worldventures.dreamtrips.modules.background_uploading.storage.CompoundOperationRepositoryImpl;
import com.worldventures.dreamtrips.modules.background_uploading.storage.CompoundOperationStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.RecentlyAddedBucketItemStorage;
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.UploadBucketPhotoInMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.FullMerchantStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.LocationStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.MerchantsStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationMemoryStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationsStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.PendingLikesStorage;
import com.worldventures.dreamtrips.modules.feed.service.storage.TranslationDiscStorage;
import com.worldventures.dreamtrips.modules.flags.storage.FlagsStorage;
import com.worldventures.dreamtrips.modules.friends.storage.CirclesStorage;
import com.worldventures.dreamtrips.modules.infopages.service.storage.DocumentsDiskStorage;
import com.worldventures.dreamtrips.modules.infopages.service.storage.DocumentsStorage;
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
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.DeviceStateActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardDetailsStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardFirmwareActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardUserActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.TermsAndConditionsStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage;
import com.worldventures.dreamtrips.wallet.di.WalletActionStorageModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
      WalletActionStorageModule.class
}, complete = false, library = true)
public class CacheActionStorageModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideLocationStorage() {
      return new LocationStorage();
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
   ActionStorage provideMerchantsStorage() {
      return new MerchantsStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFullMerchantStorage() {
      return new FullMerchantStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideWalletCardListStorage(CardListStorage cardListStorage) {
      return new WalletCardsDiskStorage(cardListStorage);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDefaultBankCardStorage(SnappyRepository snappyRepository) {
      return new DefaultBankCardStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideSmartCardStorage(SnappyRepository snappyRepository) {
      return new SmartCardActionStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideSmartCardDetailsStorage(SnappyRepository snappyRepository) {
      return new SmartCardDetailsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTermsAndConditionsStorage(SnappyRepository snappyRepository) {
      return new TermsAndConditionsStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDeviceStateActionStorage() {
      return new DeviceStateActionStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideSmartCardFirmwareActionStorage() {
      return new SmartCardFirmwareActionStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideSmartCardUserActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardUserActionStorage(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   MultipleActionStorage provideCompoundOperationStorage(CompoundOperationRepository compoundOperationRepository) {
      return new CompoundOperationStorage(compoundOperationRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFeedbackStorage(SnappyRepository db) {
      return new FeedbackTypeStorage(db);
   }

   @Singleton
   @Provides
   PendingLikesStorage provideLikesStorage() {
      return new PendingLikesStorage();
   }

   @Singleton
   @Provides
   CompoundOperationRepository provideCompoundOperationRepository(SnappyRepository snappyRepository) {
      return new CompoundOperationRepositoryImpl(snappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideDocumentsStorage(SnappyRepository db) {
      return new DocumentsStorage(new PaginatedMemoryStorage<>(), new DocumentsDiskStorage(db));
   }
}
