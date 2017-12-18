package com.worldventures.dreamtrips.social.ui.tripsimages.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeletePhotoCommand extends CommandWithError<Photo> implements InjectableAction {

   @Inject Janet janet;

   private Photo photo;

   public DeletePhotoCommand(Photo photo) {
      this.photo = photo;
   }

   @Override
   protected void run(CommandCallback<Photo> callback) throws Throwable {
      janet.createPipe(DeletePhotoHttpAction.class)
            .createObservableResult(new DeletePhotoHttpAction(photo.getUid()))
            .map(deletePhotoHttpAction -> photo)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_delete_image;
   }
}
