package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.delegate.FirmwareDelegate;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class WalletDelegateModule {

   @Singleton
   @Provides
   FirmwareDelegate provideFirmwareDelegate(SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor) {
      return new FirmwareDelegate(smartCardInteractor, firmwareInteractor);
   }

}
