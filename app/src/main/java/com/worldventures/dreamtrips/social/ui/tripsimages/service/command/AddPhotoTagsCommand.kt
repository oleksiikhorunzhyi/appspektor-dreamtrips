package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.dreamtrips.api.photos.AddUserTagsToPhotoHttpAction
import com.worldventures.dreamtrips.api.photos.model.PhotoTagParams
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import javax.inject.Inject

@CommandAction
class AddPhotoTagsCommand(private val photoUid: String, private val photoTags: List<PhotoTag>) : Command<Any>(), InjectableAction {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mapperyContext: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Any>) {
      Observable.just(mapperyContext.convert(photoTags, PhotoTagParams::class.java))
            .flatMap { janet.createPipe(AddUserTagsToPhotoHttpAction::class.java)
                  .createObservableResult(AddUserTagsToPhotoHttpAction(photoUid, it)) }
            .subscribe(callback::onSuccess, callback::onFail)
   }
}
