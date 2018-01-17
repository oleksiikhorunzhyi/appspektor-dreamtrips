package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.multimedia.DeleteVideoHttpAction
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class DeleteVideoCommand(private val video: Video) : CommandWithError<Video>(), InjectableAction {

   @Inject internal lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Video>) {
      janet.createPipe(DeleteVideoHttpAction::class.java)
            .createObservableResult(DeleteVideoHttpAction(video.uid))
            .map { video }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_delete_video
}
