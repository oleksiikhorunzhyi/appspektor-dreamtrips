package com.worldventures.dreamtrips.core.module;

import android.app.Activity;
import android.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.router.RouterImpl;
import com.worldventures.dreamtrips.core.permission.PermissionModule;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module(
      complete = false,
      library = true,
      includes = {UiUtilModule.class, PermissionModule.class})
public class ActivityModule {

   protected BaseActivity baseActivity;

   public ActivityModule(BaseActivity baseActivity) {
      this.baseActivity = baseActivity;
   }

   @Provides
   public Activity provideActivity() {
      return baseActivity;
   }

   @Provides
   public ActivityRouter provideActivityCompass() {
      return new ActivityRouter(baseActivity);
   }

   @Provides
   public android.support.v4.app.FragmentManager provideSupportFragmentManager() {
      return baseActivity.getSupportFragmentManager();
   }

   @Provides
   public FragmentManager provideFragmentManager() {
      return baseActivity.getFragmentManager();
   }

   @Provides
   public Router provideRouter() {
      return new RouterImpl(baseActivity);
   }

}
