package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.DeletePhotoHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeletePhotoCommand extends CommandWithError implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private String photoId;

   public DeletePhotoCommand(String photoId) {
      this.photoId = photoId;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(DeletePhotoHttpAction.class)
            .createObservableResult(new DeletePhotoHttpAction(photoId))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_delete_image;
   }
}
