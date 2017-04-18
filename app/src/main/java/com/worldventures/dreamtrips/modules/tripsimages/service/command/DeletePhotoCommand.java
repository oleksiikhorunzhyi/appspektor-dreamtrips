package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;
import javax.inject.Named;

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
