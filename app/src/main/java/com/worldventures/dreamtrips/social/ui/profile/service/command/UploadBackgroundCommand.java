package com.worldventures.dreamtrips.social.ui.profile.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.UpdateProfileBackgroundPhotoHttpAction;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class UploadBackgroundCommand extends CommandWithError<User> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private final File file;

   public UploadBackgroundCommand(String fileLocation) {
      this.file = new File(fileLocation);
   }

   @Override
   protected void run(CommandCallback<User> callback) throws Throwable {
      janet.createPipe(UpdateProfileBackgroundPhotoHttpAction.class)
            .createObservableResult(new UpdateProfileBackgroundPhotoHttpAction(file))
            .map(UpdateProfileBackgroundPhotoHttpAction::response)
            .map(user -> mappery.convert(user, User.class))
            .doOnNext(user -> file.delete())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_update_cover_photo;
   }
}
