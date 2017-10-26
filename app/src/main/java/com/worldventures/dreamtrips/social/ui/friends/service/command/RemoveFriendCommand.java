package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.RemoveFromFriendsHttpAction;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveFriendCommand extends CommandWithError<User> implements InjectableAction {

   private User user;

   @Inject Janet janet;

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
