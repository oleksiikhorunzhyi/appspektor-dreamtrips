package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.HideFriendRequestHttpAction;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteFriendRequestCommand extends CommandWithError<User> implements InjectableAction {

   private User user;
   private Action action;

   @Inject Janet janet;

   public DeleteFriendRequestCommand(User user, Action action) {
      this.user = user;
      this.action = action;
   }

   @Override
   protected void run(CommandCallback<User> callback) throws Throwable {
      janet.createPipe(HideFriendRequestHttpAction.class)
            .createObservableResult(new HideFriendRequestHttpAction(user.getId()))
            .subscribe(action -> {
               callback.onSuccess(user);
            }, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return action == Action.HIDE ? R.string.error_fail_to_hide_friend_request : R.string.error_fail_to_cancel_friend_request;
   }

   public enum Action {
      HIDE, CANCEL
   }
}
