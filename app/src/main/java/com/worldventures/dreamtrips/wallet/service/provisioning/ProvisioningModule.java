package com.worldventures.dreamtrips.wallet.service.provisioning;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      ProvisioningModeCommand.class,
      PinOptionalCommand.class,
},
        library = true,
        complete = false)
public class ProvisioningModule {

   @Provides
   ProvisioningModeStorage provideProvisioningStateStorage(@ForApplication Context context) {
      return new ProvisioningModeStorageImpl(context);
   }

   @Provides
   PinOptionalStorage providePinOptionalStorage(WalletStorage walletStorage) {
      return new PinOptionalStorageImpl(walletStorage);
   }
}
