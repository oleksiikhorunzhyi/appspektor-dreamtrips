package com.worldventures.dreamtrips.core.module;

import android.app.FragmentManager;

import com.worldventures.core.ui.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.media_picker.OldMediaPickerActivityModule;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            FacebookModule.class,
            OldMediaPickerActivityModule.class,
            UiUtilModule.class,
      },
      injects = {
            LaunchActivity.class,
            LaunchActivityPresenter.class,
            ActivityPresenter.class,
            Presenter.class,
      },
      complete = false,
      library = true)
public class LegacyActivityModule {

   private BaseActivity baseActivity;

   public LegacyActivityModule(BaseActivity baseActivity) {
      this.baseActivity = baseActivity;
   }

   @Provides
   android.support.v4.app.FragmentManager provideSupportFragmentManager() {
      return baseActivity.getSupportFragmentManager();
   }

   @Provides
   FragmentManager provideFragmentManager() {
      return baseActivity.getFragmentManager();
   }
}
