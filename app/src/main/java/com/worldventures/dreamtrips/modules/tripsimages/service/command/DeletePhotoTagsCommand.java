package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.RemoveUserTagsFromPhotoHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeletePhotoTagsCommand extends CommandWithError implements InjectableAction {

   @Inject Janet janet;

   private String photoId;
   private List<Integer> userIds;

   public DeletePhotoTagsCommand(String photoId, List<Integer> userIds) {
      this.photoId = photoId;
      this.userIds = userIds;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(RemoveUserTagsFromPhotoHttpAction.class)
            .createObservableResult(new RemoveUserTagsFromPhotoHttpAction(photoId, userIds))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_delete_tag;
   }
}
