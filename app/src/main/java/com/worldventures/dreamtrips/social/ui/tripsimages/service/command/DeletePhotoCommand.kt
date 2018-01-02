package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class DeletePhotoCommand(private val photo: Photo) : CommandWithError<Photo>(), InjectableAction {

   @field:Inject internal lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Photo>) {
      janet.createPipe(DeletePhotoHttpAction::class.java)
            .createObservableResult(DeletePhotoHttpAction(photo.uid))
            .map { photo }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_delete_image
}
