package com.worldventures.dreamtrips.wallet.di.external;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.WalletVideoHolderDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class WalletExternalModule {

   @Provides
   WalletVideoHolderDelegate provideVideoHolderDelegate(SessionHolder<UserSession> appSessionHolder, CachedModelHelper cachedModelHelper) {
      return new WalletVideoHolderDelegateImpl(appSessionHolder, cachedModelHelper);
   }

   @Singleton
   @Provides
   WalletSocialInfoProvider walletSocialInfoProvider(SessionHolder<UserSession> sessionHolder) {
      return new WalletSocialInfoProviderImpl(sessionHolder);
   }
}
