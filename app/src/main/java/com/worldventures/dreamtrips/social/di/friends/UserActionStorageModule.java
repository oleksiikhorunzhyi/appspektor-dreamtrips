package com.worldventures.dreamtrips.social.di.friends;

import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.social.service.friends.storage.IncomingRequestsCountStorage;
import com.worldventures.dreamtrips.social.service.friends.storage.RequestsStorage;
import com.worldventures.dreamtrips.social.service.friends.storage.UserPaginationStorage;
import com.worldventures.dreamtrips.social.service.friends.storage.UserStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module(library = true, complete = false)
public class UserActionStorageModule {
   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage providePaginationStorage() {
      return new UserPaginationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideUserStorage() {
      return new UserStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideIncomingRequestStorage() {
      return new IncomingRequestsCountStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideRequestsStorage() {
      return new RequestsStorage();
   }
}
