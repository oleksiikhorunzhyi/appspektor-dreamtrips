package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.SendFriendRequestHttpAction;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AddFriendCommand extends CommandWithError<User> implements InjectableAction {

   private User user;
   private String circleId;

   @Inject Janet janet;

   public AddFriendCommand(User user, String circleId) {
      this.user = user;
      this.circleId = circleId;
   }

   @Override
   protected void run(CommandCallback<User> callback) throws Throwable {
      janet.createPipe(SendFriendRequestHttpAction.class)
            .createObservableResult(new SendFriendRequestHttpAction(user.getId(), circleId))
            .subscribe(action -> {
               callback.onSuccess(user);
            }, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_send_friend_request;
   }
}
