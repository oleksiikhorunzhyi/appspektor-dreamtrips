package com.worldventures.dreamtrips.wallet.service.provisioning;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      ProvisioningModeCommand.class
},
        library = true,
        complete = false)
public class ProvisioningModule {

   @Provides
   ProvisioningModeStorage provisioningStateStorage(@ForApplication Context context) {
      return new ProvisioningModeStorageImpl(context);
   }
}
