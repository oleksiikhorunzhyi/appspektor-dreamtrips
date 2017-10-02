package com.worldventures.dreamtrips.social.ui.feed;

import android.content.Context;

import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.util.FileSplitter;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.AccountTimelineStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.BaseFeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.FeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.HashtagFeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.UserTimelineStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.AccountTimelineStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.UserTimelineStorageInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      BaseFeedStorageDelegate.class,
      FeedStorageDelegate.class,
      AccountTimelineStorageDelegate.class,
      UserTimelineStorageDelegate.class,
      HashtagFeedStorageDelegate.class,
      FeedEntityHolderDelegate.class
}, complete = false, library = true)
public class FeedAppModule {

   @Provides
   @Singleton
   FeedStorageDelegate provideFeedStorageDelegate(FeedStorageInteractor feedStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripsInteractor tripsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      return new FeedStorageDelegate(feedStorageInteractor, feedInteractor, postsInteractor, tripsInteractor,
            tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Provides
   @Singleton
   AccountTimelineStorageDelegate provideAccountTimelineStorageDelegate(
         AccountTimelineStorageInteractor accountTimelineStorageInteractor, FeedInteractor feedInteractor, PostsInteractor postsInteractor, TripsInteractor tripsInteractor,
         TripImagesInteractor tripImagesInteractor, BucketInteractor bucketInteractor,
         FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      return new AccountTimelineStorageDelegate(accountTimelineStorageInteractor, feedInteractor, postsInteractor, tripsInteractor,
            tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Provides
   @Singleton
   UserTimelineStorageDelegate provideUserTimelineStorageDelegate(
         UserTimelineStorageInteractor userTimelineStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripsInteractor tripsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      return new UserTimelineStorageDelegate(userTimelineStorageInteractor, feedInteractor, postsInteractor, tripsInteractor,
            tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Provides
   @Singleton
   HashtagFeedStorageDelegate provideHashtagFeedStorageDelegate(HashtagFeedStorageInteractor hashtagFeedStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripsInteractor tripsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      return new HashtagFeedStorageDelegate(hashtagFeedStorageInteractor, feedInteractor, postsInteractor, tripsInteractor,
            tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Provides
   FileSplitter provideFileSplitter(Context context) {
      return new FileSplitter(context.getExternalCacheDir());
   }
}
