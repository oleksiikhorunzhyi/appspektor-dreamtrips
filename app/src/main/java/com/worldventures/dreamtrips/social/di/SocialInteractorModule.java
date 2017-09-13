package com.worldventures.dreamtrips.social.di;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.utils.FilePathProvider;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.service.ConfigurationInteractor;
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.AccountTimelineStorageInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.UserTimelineStorageInteractor;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.media_picker.service.MediaMetadataInteractor;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.reptools.service.SuccessStoriesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.ProgressAnalyticInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(library = true, complete = false)
public class SocialInteractorModule {

   @Singleton
   @Provides
   public CirclesInteractor provideQueryCirclesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CirclesInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public LikesInteractor provideLikesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new LikesInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public CommentsInteractor provideCommentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
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
   MemberVideosInteractor provideMemberVideosInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MemberVideosInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   FeedbackInteractor provideFeedbackInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FeedbackInteractor(sessionActionPipeCreator);
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
   DocumentsInteractor provideDocumentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DocumentsInteractor(sessionActionPipeCreator);
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

   @Singleton
   @Provides
   CachedModelHelper provideCachedModelHelper(FilePathProvider filePathProvider) {
      return new CachedModelHelper(filePathProvider);
   }
}
