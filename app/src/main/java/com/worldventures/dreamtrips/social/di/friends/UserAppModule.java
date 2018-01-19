package com.worldventures.dreamtrips.social.di.friends;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor;
import com.worldventures.dreamtrips.social.service.users.friend.delegate.FriendsListStorageDelegate;
import com.worldventures.dreamtrips.social.service.users.friend.delegate.MutualFriendsStorageDelegate;
import com.worldventures.dreamtrips.social.service.users.liker.delegate.LikersStorageDelegate;
import com.worldventures.dreamtrips.social.service.users.request.delegate.RequestsStorageDelegate;
import com.worldventures.dreamtrips.social.service.users.search.delegate.SearchedUsersStorageDelegate;
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module(library = true, complete = false, includes = UserActionStorageModule.class)
public class UserAppModule {

   @Singleton
   @Provides
   public FriendsInteractor provideFriendsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FriendsInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public FriendsStorageInteractor provideFriendsStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FriendsStorageInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public RequestsStorageDelegate provideRequestStorageDelegate(FriendsInteractor friendsInteractor,
         FriendsStorageInteractor friendsStorageInteractor, CirclesInteractor circlesInteractor,
         ProfileInteractor profileInteractor) {
      return new RequestsStorageDelegate(friendsInteractor, friendsStorageInteractor, circlesInteractor, profileInteractor);
   }

   @Singleton
   @Provides
   public MutualFriendsStorageDelegate provideMutualFriendsStorageDelegate(FriendsInteractor friendsInteractor,
         FriendsStorageInteractor friendsStorageInteractor, CirclesInteractor circlesInteractor,
         ProfileInteractor profileInteractor) {
      return new MutualFriendsStorageDelegate(friendsInteractor, friendsStorageInteractor, circlesInteractor, profileInteractor);
   }

   @Singleton
   @Provides
   public FriendsListStorageDelegate provideFriendsListStorageDelegate(FriendsInteractor friendsInteractor,
         FriendsStorageInteractor friendsStorageInteractor, CirclesInteractor circlesInteractor,
         ProfileInteractor profileInteractor) {
      return new FriendsListStorageDelegate(friendsInteractor, friendsStorageInteractor, circlesInteractor, profileInteractor);
   }

   @Singleton
   @Provides
   public LikersStorageDelegate provideLikersStorageDelegate(FriendsInteractor friendsInteractor,
         FriendsStorageInteractor friendsStorageInteractor, CirclesInteractor circlesInteractor,
         ProfileInteractor profileInteractor) {
      return new LikersStorageDelegate(friendsInteractor, friendsStorageInteractor, circlesInteractor, profileInteractor);
   }

   @Singleton
   @Provides
   public SearchedUsersStorageDelegate provideSearchedUsersStorageDelegate(FriendsInteractor friendsInteractor,
         FriendsStorageInteractor friendsStorageInteractor, CirclesInteractor circlesInteractor,
         ProfileInteractor profileInteractor) {
      return new SearchedUsersStorageDelegate(friendsInteractor, friendsStorageInteractor, circlesInteractor, profileInteractor);
   }
}
