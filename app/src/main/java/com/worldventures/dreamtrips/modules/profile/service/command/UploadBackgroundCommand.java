package com.worldventures.dreamtrips.modules.profile.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.UpdateProfileBackgroundPhotoHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

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
