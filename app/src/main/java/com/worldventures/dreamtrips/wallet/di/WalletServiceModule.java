package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidBleService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@Module(complete = false, library = true)
public class WalletServiceModule {

   @Singleton
   @Provides
   WalletBluetoothService walletBluetoothService(@ForApplication Context appContext) {
      return new AndroidBleService(appContext);
   }

   @Singleton
   @Provides
   WizardInteractor provideWizardInteractor(@Named(JANET_WALLET) Janet janet) {
      return new WizardInteractor(janet);
   }

   @Singleton
   @Provides
   FirmwareInteractor firmwareInteractor(@Named(JANET_WALLET) Janet janet) {
      return new FirmwareInteractor(janet);
   }
}
