package com.worldventures.dreamtrips.wallet;

import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.wallet.di.SmartCardModule;
import com.worldventures.wallet.domain.WalletTrackingStatusStorage;
import com.worldventures.wallet.service.credentials.GoogleApiCredentials;
import com.worldventures.wallet.service.credentials.GoogleApiCredentialsProvider;
import com.worldventures.wallet.service.nxt.NxtIdConfigsProvider;
import com.worldventures.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false, includes = {
      SmartCardModule.class
})
public class WalletDtAppModule {

   @Provides
   WalletVideoHolderDelegate provideVideoHolderDelegate(CachedModelHelper cachedModelHelper) {
      return new WalletVideoHolderDelegateImpl(cachedModelHelper);
   }

   @Singleton
   @Provides
   WalletTrackingStatusStorage provideTrackingStatusStorage(SettingsStorage settingsStorage) {
      return new WalletTrackingStatusStorageImpl(settingsStorage);
   }

   @Provides
   WalletBuildConfigHelper walletBuildConfigHelper() {
      return new WalletBuildConfigHelperImpl();
   }

   @Provides
   NxtIdConfigsProvider nxtIdConfigsProvider() {
      return new NxtIdConfigsProviderImlp();
   }

   @Provides
   GoogleApiCredentialsProvider googleApiCredentialsProvider() {
      return new WalletGoogleApiCredentialsProvider();
   }
}
