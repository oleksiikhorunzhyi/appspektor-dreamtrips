package com.worldventures.dreamtrips.social.ui.profile.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.UpdateProfileAvatarHttpAction;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class UploadAvatarCommand extends CommandWithError<User> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private final String fileLocation;

   public UploadAvatarCommand(String fileLocation) {
      this.fileLocation = fileLocation;
   }

   @Override
   protected void run(CommandCallback<User> callback) throws Throwable {
      janet.createPipe(UpdateProfileAvatarHttpAction.class)
            .createObservableResult(new UpdateProfileAvatarHttpAction(new File(fileLocation)))
            .map(UpdateProfileAvatarHttpAction::response)
            .map(user -> mappery.convert(user, User.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_update_avatar;
   }
}
