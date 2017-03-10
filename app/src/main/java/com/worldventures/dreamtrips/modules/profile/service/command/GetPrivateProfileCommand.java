package com.worldventures.dreamtrips.modules.profile.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.GetCurrentUserProfileHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;
import javax.inject.Named;

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
