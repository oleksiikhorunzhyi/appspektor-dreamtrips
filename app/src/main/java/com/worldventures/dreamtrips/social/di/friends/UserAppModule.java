package com.worldventures.dreamtrips.social.di.friends;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.service.friends.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate;
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module(library = true, complete = false,
        includes = UserActionStorageModule.class)
public class UserAppModule {

   @Singleton
   @Provides
   public FriendsInteractor provideFriendsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FriendsInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public FriendStorageDelegate provideFriendStorageDelegate(FriendsInteractor friendsInteractor,
         CirclesInteractor circlesInteractor, ProfileInteractor profileInteractor) {
      return new FriendStorageDelegate(friendsInteractor, circlesInteractor, profileInteractor);
   }
}
