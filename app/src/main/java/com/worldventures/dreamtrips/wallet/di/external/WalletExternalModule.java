package com.worldventures.dreamtrips.wallet.di.external;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class WalletExternalModule {

   @Provides
   WalletVideoHolderDelegate provideVideoHolderDelegate(SessionHolder appSessionHolder, CachedModelHelper cachedModelHelper) {
      return new WalletVideoHolderDelegateImpl(appSessionHolder, cachedModelHelper);
   }

   @Singleton
   @Provides
   WalletSocialInfoProvider walletSocialInfoProvider(SessionHolder sessionHolder) {
      return new WalletSocialInfoProviderImpl(sessionHolder);
   }

   @Singleton
   @Provides
   WalletTrackingStatusStorage provideTrackingStatusStorage(SettingsStorage settingsStorage) {
      return new WalletTrackingStatusStorageImpl(settingsStorage);
   }
}
