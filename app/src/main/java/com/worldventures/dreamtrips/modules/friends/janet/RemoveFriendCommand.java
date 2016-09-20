package com.worldventures.dreamtrips.modules.friends.janet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.RemoveFromFriendsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveFriendCommand extends CommandWithError<User> implements InjectableAction {

   private User user;

   @Named(JanetModule.JANET_API_LIB) @Inject Janet janet;

   public RemoveFriendCommand(User user) {
      this.user = user;
   }

   @Override
   protected void run(CommandCallback<User> callback) throws Throwable {
      janet.createPipe(RemoveFromFriendsHttpAction.class)
            .createObservableResult(new RemoveFromFriendsHttpAction(user.getId()))
            .subscribe(action -> {
               callback.onSuccess(user);
            }, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_unfriend_user;
   }
}
