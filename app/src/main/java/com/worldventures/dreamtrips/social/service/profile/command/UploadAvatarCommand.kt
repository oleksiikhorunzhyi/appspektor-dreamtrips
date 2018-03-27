package com.worldventures.dreamtrips.social.service.profile.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.User
import com.worldventures.core.model.session.ImmutableUserSession
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.profile.UpdateProfileAvatarHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.io.File
import javax.inject.Inject

@CommandAction
class UploadAvatarCommand(private val fileLocation: String) : CommandWithError<User>(), InjectableAction {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mappery: MapperyContext
   @Inject lateinit var appSessionHolder: SessionHolder

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<User>) {
      janet.createPipe(UpdateProfileAvatarHttpAction::class.java)
            .createObservableResult(UpdateProfileAvatarHttpAction(File(fileLocation)))
            .map(UpdateProfileAvatarHttpAction::response)
            .map { mappery.convert(it, User::class.java) }
            .doOnNext {
               appSessionHolder.put(ImmutableUserSession.builder()
                     .from(appSessionHolder.get().get()).user(it).build())
            }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_update_avatar

}
