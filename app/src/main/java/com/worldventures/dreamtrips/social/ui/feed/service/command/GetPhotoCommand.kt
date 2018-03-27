package com.worldventures.dreamtrips.social.ui.feed.service.command

import com.worldventures.core.service.command.api_action.MappableApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.photos.GetPhotoHttpAction
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetPhotoCommand(val uid: String) : MappableApiActionCommand<GetPhotoHttpAction, Photo, Photo>() {

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_item_details

   override fun getMappingTargetClass(): Class<Photo> = Photo::class.java

   override fun mapHttpActionResult(httpAction: GetPhotoHttpAction): Any = httpAction.response()

   override fun getHttpAction(): GetPhotoHttpAction = GetPhotoHttpAction(uid)

   override fun getHttpActionClass(): Class<GetPhotoHttpAction> = GetPhotoHttpAction::class.java

}
