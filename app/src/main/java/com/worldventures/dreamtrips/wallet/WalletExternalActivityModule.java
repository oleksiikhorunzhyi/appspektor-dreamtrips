package com.worldventures.dreamtrips.wallet;

import android.app.Activity;

import com.worldventures.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.wallet.ui.WalletActivityModule;
import com.worldventures.wallet.ui.common.WalletNavigationDelegate;
import com.worldventures.wallet.ui.common.navigation.CoreNavigator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            DTWalletActivity.class,
      },
      includes = {
            WalletActivityModule.class,
      },
      library = true, complete = false)
public class WalletExternalActivityModule {

   @Provides
   CoreNavigator provideCoreNavigator(Activity activity, Router coreRouter) {
      return new CoreNavigatorImpl(activity, coreRouter);
   }

   @Provides
   @Singleton
   WalletNavigationDelegate provideNavigationDelegate(NavigationDrawerPresenter navigationDrawerPresenter,
         RootComponentsProvider rootComponentsProvider, ActivityRouter activityRouter, Activity activity) {
      return new DTDrawerNavigation(navigationDrawerPresenter, rootComponentsProvider, activityRouter, activity);
   }
}
