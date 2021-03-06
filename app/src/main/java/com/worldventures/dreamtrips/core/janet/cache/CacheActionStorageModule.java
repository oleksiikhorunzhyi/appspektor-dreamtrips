package com.worldventures.dreamtrips.core.janet.cache;

import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.core.modules.facebook.service.storage.FacebookAlbumsStorage;
import com.worldventures.core.modules.facebook.service.storage.FacebookPhotosStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.service.storage.NotificationCounterStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.FullMerchantStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.LocationStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.MerchantsStorage;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.TransactionsStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class CacheActionStorageModule {
   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideLocationStorage() {
      return new LocationStorage();
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
   ActionStorage provideFacebookAlbumsStorage() {
      return new FacebookAlbumsStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFacebookPhotosStorage() {
      return new FacebookPhotosStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideNotificationCounterStorage(SnappyRepository socialSnappyRepository) {
      return new NotificationCounterStorage(socialSnappyRepository);
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideTransactionsStorage() {
      return new TransactionsStorage();
   }
}
