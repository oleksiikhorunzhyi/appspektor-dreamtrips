package com.worldventures.dreamtrips.social.service.profile.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.User
import com.worldventures.core.model.session.ImmutableUserSession
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.profile.UpdateProfileBackgroundPhotoHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.io.File
import javax.inject.Inject

@CommandAction
class UploadBackgroundCommand(fileLocation: String) : CommandWithError<User>(), InjectableAction {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mappery: MapperyContext
   @Inject lateinit var appSessionHolder: SessionHolder
   private val file: File = File(fileLocation)

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<User>) {
      janet.createPipe(UpdateProfileBackgroundPhotoHttpAction::class.java)
            .createObservableResult(UpdateProfileBackgroundPhotoHttpAction(file))
            .map(UpdateProfileBackgroundPhotoHttpAction::response)
            .map { mappery.convert(it, User::class.java) }
            .doOnNext {
               file.delete()
               appSessionHolder.put(ImmutableUserSession.builder()
                     .from(appSessionHolder.get().get()).user(it).build())
            }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_update_cover_photo

}
