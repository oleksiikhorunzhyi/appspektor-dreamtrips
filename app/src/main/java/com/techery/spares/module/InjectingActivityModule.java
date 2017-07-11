package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.activity.InjectingActivity;

import dagger.Module;
import dagger.Provides;

@Module(
      library = true,
      complete = false,
      addsTo = InjectingApplicationModule.class)
public class InjectingActivityModule {
   private final InjectingActivity activity;
   private final Injector injector;

   public InjectingActivityModule(InjectingActivity activity, Injector injector) {
      this.activity = activity;
      this.injector = injector;
   }

   @ForActivity
   @Provides
   Context provideActivityContext() {
      return activity;
   }

   @Provides
   InjectingActivity provideActivity() {
      return activity;
   }

   @ForActivity
   @Provides
   Injector provideActivityInjector() {
      return injector;
   }
}
