package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {NavigationDrawerPresenter.class,}, library = true, complete = false)
public class NavigationModule {

   @Provides
   @Singleton
   public BackStackDelegate backStackDelegate() {
      return new BackStackDelegate();
   }

}
