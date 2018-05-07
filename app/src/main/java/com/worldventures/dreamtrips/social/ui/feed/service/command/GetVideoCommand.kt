package com.worldventures.dreamtrips.social.ui.feed.service.command

import com.worldventures.core.service.command.api_action.MappableApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.multimedia.GetVideoHttpAction
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetVideoCommand(val uid: String) : MappableApiActionCommand<GetVideoHttpAction, Video, Video>() {

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_item_details

   override fun getMappingTargetClass(): Class<Video> = Video::class.java

   override fun mapHttpActionResult(httpAction: GetVideoHttpAction): Any = httpAction.response()

   override fun getHttpAction(): GetVideoHttpAction = GetVideoHttpAction(uid)

   override fun getHttpActionClass(): Class<GetVideoHttpAction> = GetVideoHttpAction::class.java

}
