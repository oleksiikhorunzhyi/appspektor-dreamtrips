package com.worldventures.dreamtrips.modules.media_picker;

import android.content.Context;

import com.worldventures.dreamtrips.modules.media_picker.service.delegate.PhotosProvider;
import com.worldventures.dreamtrips.modules.media_picker.service.delegate.PhotosProviderImpl;
import com.worldventures.dreamtrips.modules.media_picker.service.delegate.VideosProvider;
import com.worldventures.dreamtrips.modules.media_picker.service.delegate.VideosProviderImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {

}, library = true, complete = false)
public class MediaPickerModule {

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
}
