package com.worldventures.dreamtrips.social.ui.feed;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.social.ui.background_uploading.util.FileSplitter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.AccountTimelineStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.BaseFeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.FeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.HashtagFeedStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.UserTimelineStorageDelegate;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.AccountTimelineStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.UserTimelineStorageInteractor;

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
   FeedStorageDelegate provideFeedStorageDelegate(FeedStorageInteractor feedStorageInteractor, @ForApplication Injector injector) {
      return new FeedStorageDelegate(feedStorageInteractor, injector);
   }

   @Provides
   @Singleton
   AccountTimelineStorageDelegate provideAccountTimelineStorageDelegate(
         AccountTimelineStorageInteractor accountTimelineStorageInteractor, @ForApplication Injector injector) {
      return new AccountTimelineStorageDelegate(accountTimelineStorageInteractor, injector);
   }

   @Provides
   @Singleton
   UserTimelineStorageDelegate provideUserTimelineStorageDelegate(
         UserTimelineStorageInteractor userTimelineStorageInteractor, @ForApplication Injector injector) {
      return new UserTimelineStorageDelegate(userTimelineStorageInteractor, injector);
   }

   @Provides
   @Singleton
   HashtagFeedStorageDelegate provideHashtagFeedStorageDelegate(HashtagFeedStorageInteractor hashtagFeedStorageInteractor,
         @ForApplication Injector injector) {
      return new HashtagFeedStorageDelegate(hashtagFeedStorageInteractor, injector);
   }

   @Provides
   FileSplitter provideFileSplitter(Context context) {
      return new FileSplitter(context.getExternalCacheDir());
   }
}
