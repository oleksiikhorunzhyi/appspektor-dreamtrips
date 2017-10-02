package com.worldventures.core.ui.util.permission;

import android.app.Activity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class PermissionModule {

   @Singleton
   @Provides
   PermissionDispatcher providePermissionDispatcher(Activity activity) {
      return new PermissionDispatcher(activity);
   }
}
