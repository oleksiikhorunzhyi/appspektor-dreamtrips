package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.PersistentDeviceStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.StorageModule;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.smartcard.client.NxtSmartCardClient;
import io.techery.janet.smartcard.client.SmartCardClient;
import io.techery.janet.smartcard.mock.client.MockSmartCardClient;

@Module(
      includes = {
            WalletServiceModule.class,
            StorageModule.class,
            JanetNxtModule.class
      },
      complete = false, library = true
)
public class SmartCardModule {

   @Provides
   NxtSmartCardClient provideNxtSmartCardClient(@ForApplication Context context) {
      return new NxtSmartCardClient(context);
   }

   @Provides
   MockSmartCardClient provideMockSmartCardClient(SnappyRepository db) {
      return new MockSmartCardClient(() -> PersistentDeviceStorage.load(db));
   }

   @Singleton
   @Provides
   SmartCardClient provideSmartCardClient(Provider<NxtSmartCardClient> nxtProvider, Provider<MockSmartCardClient> mockProvider) {
      return BuildConfig.SMART_CARD_SDK_CLIENT.equals("nxtid") ? nxtProvider.get() : mockProvider.get();
   }
}
