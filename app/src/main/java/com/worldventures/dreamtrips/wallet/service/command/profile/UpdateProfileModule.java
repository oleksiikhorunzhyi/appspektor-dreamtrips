package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Singleton
@Module(
      injects = {
            UpdateSmartCardUserCommand.class,
            RevertSmartCardUserUpdatingCommand.class,
            RetryHttpUploadUpdatingCommand.class
      },
      library = true, complete = false)
public class UpdateProfileModule {

   @Provides
   @Singleton
   UpdateDataHolder updateDataHolder() {
      return new UpdateDataHolder();
   }

   @Provides
   @Singleton
   UpdateProfileManager updateProfileManager(
         Janet janetApi,
         SmartCardInteractor smartCardInteractor,
         UpdateDataHolder updateDataHolder) {
      return new UpdateProfileManager(janetApi, smartCardInteractor, updateDataHolder);
   }
}
