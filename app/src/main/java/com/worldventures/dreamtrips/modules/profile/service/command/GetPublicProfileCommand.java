package com.worldventures.dreamtrips.modules.profile.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.profile.GetPublicUserProfileHttpAction;
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
public class GetPublicProfileCommand extends CommandWithError<User> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
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
