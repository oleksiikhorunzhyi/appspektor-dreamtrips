package com.worldventures.dreamtrips.modules.media_picker;

import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.modules.media_picker.presenter.MediaPickerPresenter;
import com.worldventures.dreamtrips.modules.media_picker.view.fragment.MediaPickerFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      MediaPickerFragment.class,
      MediaPickerPresenter.class,
      PhotoPickerLayout.class,}, complete = false, library = true)
public class OldMediaPickerActivityModule {

   @Provides
   @Singleton
   PhotoPickerLayoutDelegate providePhotoPickerLayoutDelegate(BackStackDelegate backStackDelegate) {
      return new PhotoPickerLayoutDelegate(backStackDelegate);
   }
}
