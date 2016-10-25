package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.FactoryResetManager;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidBleService;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidNetworkManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@Module(complete = false, library = true)
public class WalletServiceModule {

   @Named(JANET_WALLET)
   @Singleton
   @Provides
   SessionActionPipeCreator pipeCreator(@Named(JANET_WALLET) Janet janet) {
      return new SessionActionPipeCreator(janet);
   }

   @Singleton
   @Provides
   WalletBluetoothService walletBluetoothService(@ForApplication Context appContext) {
      return new AndroidBleService(appContext);
   }

   @Singleton
   @Provides
   WalletNetworkService walletNetworkService(@ForApplication Context appContext) {
      return new AndroidNetworkManager(appContext);
   }

   @Singleton
   @Provides
   WizardInteractor provideWizardInteractor(@Named(JANET_WALLET) Janet janet, @Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new WizardInteractor(janet, sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   SmartCardInteractor provideSmartCardInteractor(@Named(JANET_WALLET) Janet janet, @Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new SmartCardInteractor(janet, sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   FirmwareInteractor firmwareInteractor(@Named(JANET_WALLET) Janet janet) {
      return new FirmwareInteractor(janet);
   }

   @Singleton
   @Provides
   FactoryResetManager factoryResetManager(@Named(JANET_WALLET) Janet janet) {
      return new FactoryResetManager(janet);
   }
}
