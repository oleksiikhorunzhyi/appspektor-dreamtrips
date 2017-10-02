package com.worldventures.dreamtrips.social.di;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.PostLocationPickerCallback;
import com.worldventures.dreamtrips.social.ui.podcast_player.delegate.PodcastPlayerDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.delegate.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.MediaRefresher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SocialDelegateModule {

   @Provides
   @Singleton
   MediaRefresher provideMemberImagesRefresher(TripImagesInteractor tripImagesInteractor, TripImagesCommandFactory tripImagesCommandFactory) {
      return new MediaRefresher(tripImagesInteractor, tripImagesCommandFactory);
   }

   @Provides
   TripImagesCommandFactory provideTripImagesCommandFactory() {
      return new TripImagesCommandFactory();
   }

   @Provides
   @Singleton
   PodcastPlayerDelegate providePodcastPlayerDelegate(@ForApplication Context context) {
      return new PodcastPlayerDelegate(context);
   }

   @Provides
   @Singleton
   SocialCropImageManager provideGlobalConfigManager() {
      return new SocialCropImageManager();
   }

   @Provides
   @Singleton
   EditPhotoTagsCallback provideEditPhotoTagsCallback() {
      return new EditPhotoTagsCallback();
   }

   @Provides
   @Singleton
   PostLocationPickerCallback providePostLocationPickerCallback() {
      return new PostLocationPickerCallback();
   }
}
