package com.worldventures.dreamtrips.social.di;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.media_picker.service.MediaMetadataInteractor;
import com.worldventures.dreamtrips.social.service.InviteShareInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.AccountTimelineStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.UserTimelineStorageInteractor;
import com.worldventures.dreamtrips.social.ui.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.CirclesInteractor;
import com.worldventures.dreamtrips.social.ui.membership.service.PodcastsInteractor;
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.social.ui.reptools.service.SuccessStoriesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.ProgressAnalyticInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.video.service.ConfigurationInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(library = true, complete = false)
class SocialInteractorModule {

   @Singleton
   @Provides
   CirclesInteractor provideQueryCirclesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CirclesInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
    LikesInteractor provideLikesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new LikesInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   CommentsInteractor provideCommentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CommentsInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   BucketInteractor provideBucketInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new BucketInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   TripImagesInteractor provideTripImagesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new TripImagesInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   SuccessStoriesInteractor provideSuccessStoriesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new SuccessStoriesInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ProfileInteractor provideProfileInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         SessionHolder sessionHolder) {
      return new ProfileInteractor(sessionActionPipeCreator, sessionHolder);
   }

   @Provides
   @Singleton
   PostsInteractor providePostsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new PostsInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   OfflineErrorInteractor provideOfflineErrorInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new OfflineErrorInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ProgressAnalyticInteractor provideVideoInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ProgressAnalyticInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   AppConfigurationInteractor provideVersionCheckInteractor(Janet janet) {
      return new AppConfigurationInteractor(janet);
   }

   @Provides
   @Singleton
   FeedStorageInteractor provideFeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FeedStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   AccountTimelineStorageInteractor provideTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new AccountTimelineStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   UserTimelineStorageInteractor provideUserTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new UserTimelineStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   HashtagFeedStorageInteractor provideHashtagFeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new HashtagFeedStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ConfigurationInteractor provideConfigurationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ConfigurationInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   MediaMetadataInteractor provideMediaMetadataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MediaMetadataInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ActiveFeedRouteInteractor provideActiveFeedRouteInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ActiveFeedRouteInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   FlagsInteractor provideFlagsProvider(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FlagsInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   InviteShareInteractor provideInviteShareInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new InviteShareInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton PodcastsInteractor providePodcastInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new PodcastsInteractor(sessionActionPipeCreator);
   }
}
