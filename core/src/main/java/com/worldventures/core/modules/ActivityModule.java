package com.worldventures.core.modules;

import android.app.Activity;

import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.picker.MediaPickerActivityModule;
import com.worldventures.core.ui.view.activity.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            MediaPickerActivityModule.class,
      },
      complete = false,
      library = true)
public class ActivityModule {

   private BaseActivity baseActivity;

   public ActivityModule(BaseActivity baseActivity) {
      this.baseActivity = baseActivity;
   }

   @Provides
   public Activity provideActivity() {
      return baseActivity;
   }

   @ForActivity
   @Provides
   Injector provideActivityInjector() { // I think this Injector is unused
      return baseActivity;
   }
}
