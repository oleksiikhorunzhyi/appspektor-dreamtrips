package com.worldventures.dreamtrips.wallet.di.external;

import android.app.Activity;

import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.CoreNavigator;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class WalletExternalActivityModule {

   @Provides
   CoreNavigator provideCoreNavigator(Activity activity, Router coreRouter) {
      return new CoreNavigatorImpl(activity, coreRouter);
   }
}
