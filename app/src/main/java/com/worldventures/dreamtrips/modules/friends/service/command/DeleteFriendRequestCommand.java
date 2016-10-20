package com.worldventures.dreamtrips.modules.friends.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.HideFriendRequestHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteFriendRequestCommand extends CommandWithError<User> implements InjectableAction {

   private User user;
   private Action action;

   @Named(JanetModule.JANET_API_LIB) @Inject Janet janet;

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
