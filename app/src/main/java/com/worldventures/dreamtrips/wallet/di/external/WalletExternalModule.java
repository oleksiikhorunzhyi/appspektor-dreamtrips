package com.worldventures.dreamtrips.wallet.di.external;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.social.util.CachedModelHelper;
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
   WalletTrackingStatusStorage provideTrackingStatusStorage(SnappyRepository snappyRepository) {
      return new WalletTrackingStatusStorageImpl(snappyRepository);
   }
}
