package com.worldventures.dreamtrips.wallet.service.provisioning;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      ProvisioningModeCommand.class,
      PinOptionalCommand.class
},
        library = true,
        complete = false)
public class ProvisioningModule {

   @Provides
   ProvisioningModeStorage provideProvisioningStateStorage(@ForApplication Context context) {
      return new ProvisioningModeStorageImpl(context);
   }

   @Provides
   PinOptionalStorage providePinOptionalStorage(SnappyRepository snappyRepository) {
      return new PinOptionalStorageImpl(snappyRepository);
   }
}
