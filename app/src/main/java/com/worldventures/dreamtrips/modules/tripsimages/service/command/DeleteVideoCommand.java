package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.multimedia.DeleteVideoHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteVideoCommand extends CommandWithError<Video> implements InjectableAction {

   @Inject Janet janet;

   private Video video;

   public DeleteVideoCommand(Video video) {
      this.video = video;
   }

   @Override
   protected void run(CommandCallback<Video> callback) throws Throwable {
      janet.createPipe(DeleteVideoHttpAction.class)
            .createObservableResult(new DeleteVideoHttpAction(video.getUid()))
            .map(deletePhotoHttpAction -> video)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_delete_video;
   }
}
