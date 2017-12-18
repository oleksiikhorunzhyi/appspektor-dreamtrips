package com.worldventures.wallet.service.profile

import com.worldventures.wallet.di.WalletJanetModule
import com.worldventures.wallet.service.SmartCardInteractor
import dagger.Module
import dagger.Provides
import io.techery.janet.Janet
import io.techery.mappery.MapperyContext
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Module(injects = arrayOf(
      UpdateSmartCardUserCommand::class,
      RevertSmartCardUserUpdatingCommand::class,
      RetryHttpUploadUpdatingCommand::class,
      UpdateSmartCardUserPhotoCommand::class),
      library = true, complete = false)
class UpdateProfileModule {

   @Provides
   @Singleton
   fun updateProfileManager(@Named(WalletJanetModule.JANET_WALLET) janetApi: Janet,
                            mapperyContext: MapperyContext,
                            smartCardInteractor: SmartCardInteractor): UpdateProfileManager =
         UpdateProfileManager(janetApi, mapperyContext, smartCardInteractor)
}
