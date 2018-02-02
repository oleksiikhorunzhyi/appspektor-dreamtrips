package com.worldventures.dreamtrips.social.di.friends;

import com.worldventures.dreamtrips.social.service.users.friend.storage.FriendsListStorage;
import com.worldventures.dreamtrips.social.service.users.friend.storage.GetFriendsPaginationStorage;
import com.worldventures.dreamtrips.social.service.users.friend.storage.GetMutualFriendsPaginationStorage;
import com.worldventures.dreamtrips.social.service.users.friend.storage.MutualFriendsStorage;
import com.worldventures.dreamtrips.social.service.users.liker.storage.GetLikersPaginationStorage;
import com.worldventures.dreamtrips.social.service.users.liker.storage.LikersStorage;
import com.worldventures.dreamtrips.social.service.users.request.storage.GetRequestsPaginationStorage;
import com.worldventures.dreamtrips.social.service.users.request.storage.RequestsStorage;
import com.worldventures.dreamtrips.social.service.users.request.storage.UserRequestsStorage;
import com.worldventures.dreamtrips.social.service.users.search.storage.SearchUsersPaginationStorage;
import com.worldventures.dreamtrips.social.service.users.search.storage.SearchedUsersStorage;
import com.worldventures.janet.cache.storage.ActionStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module(library = true, complete = false)
public class UserActionStorageModule {

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFriendsListPaginationStorage() {
      return new GetFriendsPaginationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideSearchedUsersPaginationStorage() {
      return new SearchUsersPaginationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideRequestsPaginationStorage() {
      return new GetRequestsPaginationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideMutualFriendsPaginationStorage() {
      return new GetMutualFriendsPaginationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideLikersPaginationStorage() {
      return new GetLikersPaginationStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideFriendListStorage() {
      return new FriendsListStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideSearchedUsersStorage() {
      return new SearchedUsersStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideUserRequestsStorage() {
      return new UserRequestsStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideLikersStorage() {
      return new LikersStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideMutualFriendsStorage() {
      return new MutualFriendsStorage();
   }

   @Singleton
   @Provides(type = Provides.Type.SET)
   ActionStorage provideRequestsStorage() {
      return new RequestsStorage();
   }
}
