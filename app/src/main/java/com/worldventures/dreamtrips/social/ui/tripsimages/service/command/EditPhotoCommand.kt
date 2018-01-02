package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.dreamtrips.api.photos.UpdatePhotoHttpAction
import com.worldventures.dreamtrips.api.photos.model.PhotoUpdateParams
import com.worldventures.dreamtrips.modules.common.model.UploadTask
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import javax.inject.Inject

@CommandAction
class EditPhotoCommand(private val uid: String, private val task: UploadTask) : Command<Photo>(), InjectableAction {

   @field:Inject internal lateinit var janet: Janet
   @field:Inject internal lateinit var mapperyContext: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Photo>) {
      Observable.just(mapperyContext.convert(task, PhotoUpdateParams::class.java))
            .flatMap {
               janet.createPipe(UpdatePhotoHttpAction::class.java)
                     .createObservableResult(UpdatePhotoHttpAction(uid, it))
            }
            .map { mapperyContext.convert(it.response(), Photo::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }
}
