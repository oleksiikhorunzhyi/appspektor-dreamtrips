package com.worldventures.dreamtrips.wallet.di.external;

import com.worldventures.dreamtrips.wallet.ui.common.navigation.CoreNavigator;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class WalletExternalActivityModule {

   @Provides
   CoreNavigator provideCoreNavigator(com.worldventures.dreamtrips.core.navigation.router.Router coreRouter) {
      return new CoreNavigatorImpl(coreRouter);
   }
}
