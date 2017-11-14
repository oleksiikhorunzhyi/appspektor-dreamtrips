package com.worldventures.dreamtrips.social.ui.profile.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.GetCurrentUserProfileHttpAction;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetPrivateProfileCommand extends CommandWithError<User> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetCurrentUserProfileHttpAction.class)
            .createObservableResult(new GetCurrentUserProfileHttpAction())
            .map(GetCurrentUserProfileHttpAction::response)
            .map(user -> mappery.convert(user, User.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_profile_info;
   }
}
