package com.worldventures.wallet.service.profile

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.SmartCardUser

import javax.inject.Inject

import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class RetryHttpUploadUpdatingCommand(smartCardId: String, newUser: SmartCardUser
) : BaseUserUpdateCommand<SmartCardUser>(smartCardId, newUser), InjectableAction {

   @Inject lateinit var updateProfileManager: UpdateProfileManager

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCardUser>) {
      updateProfileManager.uploadData(smartCardId, newUser)
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }
}
