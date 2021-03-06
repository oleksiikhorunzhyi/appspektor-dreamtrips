package com.worldventures.wallet.di;

import android.content.Context;

import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.converter.SmartCardConverterModule;
import com.worldventures.wallet.domain.session.NxtSessionHolder;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.domain.storage.WalletStorageModule;
import com.worldventures.wallet.domain.storage.action.PersistentDeviceStorage;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.WalletSocialInfoProviderImpl;
import com.worldventures.wallet.service.logout.WalletLogoutActionModule;
import com.worldventures.wallet.service.nxt.JanetNxtModule;
import com.worldventures.wallet.util.WalletBuildConfigHelper;
import com.worldventures.wallet.util.WalletFeatureHelper;
import com.worldventures.wallet.util.WalletFeatureHelperFull;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.smartcard.client.NxtSmartCardClient;
import io.techery.janet.smartcard.client.SmartCardClient;
import io.techery.janet.smartcard.mock.client.MockSmartCardClient;

import static com.worldventures.wallet.domain.WalletConstants.WALLET_COMPONENT;

@Module(
      includes = {
            WalletJanetModule.class,
            WalletServiceModule.class,
            WalletStorageModule.class,
            JanetNxtModule.class,
            SmartCardConverterModule.class,
            WalletLogoutActionModule.class,
            WalletInitializerModule.class,
      }, complete = false, library = true
)
public class SmartCardModule {

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideWalletComponent() {
      return new ComponentDescription.Builder()
            .key(WALLET_COMPONENT)
            .navMenuTitle(R.string.wallet)
            .toolbarTitle(R.string.wallet)
            .icon(R.drawable.ic_wallet)
            .skipGeneralToolbar(true)
            .shouldFinishMainActivity(true)
            .build();
   }

   @Provides
   NxtSmartCardClient provideNxtSmartCardClient(@ForApplication Context context) {
      return new NxtSmartCardClient(context);
   }

   @Provides
   MockSmartCardClient provideMockSmartCardClient(WalletStorage db) {
      return new MockSmartCardClient(() -> PersistentDeviceStorage.load(db));
   }

   @Singleton
   @Provides
   SmartCardClient provideSmartCardClient(Provider<NxtSmartCardClient> nxtProvider, Provider<MockSmartCardClient> mockProvider, WalletBuildConfigHelper configHelper) {
      return configHelper.useNxtClient() ? nxtProvider.get() : mockProvider.get();
   }

   @Singleton
   @Provides
   WalletFeatureHelper featureHelper() {
      //      return new WalletFeatureHelperRelease();
      return new WalletFeatureHelperFull();
   }

   @Provides
   @Singleton
   NxtSessionHolder provideNxtSessionHolder(SimpleKeyValueStorage simpleKeyValueStorage) {
      return new NxtSessionHolder(simpleKeyValueStorage);
   }

   @Singleton
   @Provides
   WalletSocialInfoProvider walletSocialInfoProvider(SessionHolder sessionHolder) {
      return new WalletSocialInfoProviderImpl(sessionHolder);
   }


}
