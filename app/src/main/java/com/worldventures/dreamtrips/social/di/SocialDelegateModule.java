package com.worldventures.dreamtrips.social.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.delegate.system.UriPathProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.UriPathProviderImpl;
import com.worldventures.dreamtrips.modules.common.service.UploadingFileManager;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.PostLocationPickerCallback;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.delegate.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommandFactory;
import com.worldventures.dreamtrips.modules.tripsimages.service.delegate.MediaRefresher;

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
   UploadingFileManager provideUploadingFileManager(Context context) {
      return new UploadingFileManager(context.getFilesDir());
   }

   @Provides
   @Singleton
   UriPathProvider provideUriPathProvider(Context context) {
      return new UriPathProviderImpl(context);
   }

   @Provides
   @Singleton
   CachedEntityDelegate provideDownloadFileDelegate(CachedEntityInteractor cachedEntityInteractor) {
      return new CachedEntityDelegate(cachedEntityInteractor);
   }

   @Provides
   @Singleton
   CachedEntityInteractor provideDownloadCachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CachedEntityInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   DownloadFileInteractor provideDownloadFileInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DownloadFileInteractor(sessionActionPipeCreator);
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
