package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import javax.inject.Named;
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
         @Named(JanetModule.JANET_API_LIB) Janet janetApi,
         @Named(JanetModule.JANET_WALLET) Janet janetWallet,
         SnappyRepository snappyRepository, UpdateDataHolder updateDataHolder) {
      return new UpdateProfileManager(janetApi, janetWallet, snappyRepository, updateDataHolder);
   }
}
