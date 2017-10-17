package com.worldventures.core.modules.picker;

import android.content.Context;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.ServiceModule;
import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand;
import com.worldventures.core.modules.picker.command.GetPhotosFromGalleryCommand;
import com.worldventures.core.modules.picker.command.GetVideoDurationCommand;
import com.worldventures.core.modules.picker.command.GetVideosFromGalleryCommand;
import com.worldventures.core.modules.picker.command.MediaAttachmentPrepareCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.delegate.PhotosProvider;
import com.worldventures.core.modules.picker.service.delegate.PhotosProviderImpl;
import com.worldventures.core.modules.picker.service.delegate.VideosProvider;
import com.worldventures.core.modules.picker.service.delegate.VideosProviderImpl;
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper;
import com.worldventures.core.ui.util.DrawableUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            GetPhotosFromGalleryCommand.class,
            GetVideosFromGalleryCommand.class,
            MediaAttachmentPrepareCommand.class,
            GetMediaFromGalleryCommand.class,
            GetVideoDurationCommand.class,
            CopyFileCommand.class,
      },

      includes = {
            ServiceModule.class,
      },
      complete = false, library = true)
public class MediaPickerAppModule {

   @Singleton
   @Provides
   PhotosProvider providePhotosProvider(Context context) {
      return new PhotosProviderImpl(context);
   }

   @Singleton
   @Provides
   VideosProvider provideVideosProvider(Context context) {
      return new VideosProviderImpl(context);
   }

   @Provides
   @Singleton
   MediaPickerInteractor provideMediaInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MediaPickerInteractor(sessionActionPipeCreator);
   }

   @Provides
   CapturedRowMediaHelper provideCapturedRowMediaHelper(MediaPickerInteractor mediaPickerInteractor, DrawableUtil drawableUtil) {
      return new CapturedRowMediaHelper(mediaPickerInteractor, drawableUtil);
   }
}
