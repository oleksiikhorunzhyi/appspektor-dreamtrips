package com.worldventures.dreamtrips.social.ui.profile.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.GetPublicUserProfileHttpAction;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetPublicProfileCommand extends CommandWithError<User> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private int userId;

   public GetPublicProfileCommand(int userId) {
      this.userId = userId;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetPublicUserProfileHttpAction.class)
            .createObservableResult(new GetPublicUserProfileHttpAction(userId))
            .map(GetPublicUserProfileHttpAction::response)
            .map(user -> mappery.convert(user, User.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_profile_info;
   }
}
