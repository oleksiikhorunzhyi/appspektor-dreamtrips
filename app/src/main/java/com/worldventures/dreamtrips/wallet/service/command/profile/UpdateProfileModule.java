package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.core.janet.JanetModule;

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
         Janet janetApi,
         @Named(JanetModule.JANET_WALLET) Janet janetWallet,
         UpdateDataHolder updateDataHolder) {
      return new UpdateProfileManager(janetApi, janetWallet, updateDataHolder);
   }
}
