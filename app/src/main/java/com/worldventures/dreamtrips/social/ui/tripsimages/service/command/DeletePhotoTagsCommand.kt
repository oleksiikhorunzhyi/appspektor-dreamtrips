package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.photos.RemoveUserTagsFromPhotoHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class DeletePhotoTagsCommand(private val photoId: String, private val userIds: List<Int>) : CommandWithError<Any>(), InjectableAction {

   @field:Inject internal lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Any>) {
      janet.createPipe(RemoveUserTagsFromPhotoHttpAction::class.java)
            .createObservableResult(RemoveUserTagsFromPhotoHttpAction(photoId, userIds))
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_delete_tag
}
