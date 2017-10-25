package com.worldventures.dreamtrips.modules.media_picker;

import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class OldMediaPickerModule {

   @Provides
   @Singleton
   PhotoPickerDelegate providePhotoPickerDelegate() {
      return new PhotoPickerDelegate();
   }

}
