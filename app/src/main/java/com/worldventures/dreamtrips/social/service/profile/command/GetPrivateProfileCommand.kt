package com.worldventures.dreamtrips.social.service.profile.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.profile.GetCurrentUserProfileHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

@CommandAction
class GetPrivateProfileCommand : CommandWithError<User>(), InjectableAction {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mappery: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<User>) {
      janet.createPipe(GetCurrentUserProfileHttpAction::class.java)
            .createObservableResult(GetCurrentUserProfileHttpAction())
            .map(GetCurrentUserProfileHttpAction::response)
            .map { mappery.convert(it, User::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_profile_info
}
