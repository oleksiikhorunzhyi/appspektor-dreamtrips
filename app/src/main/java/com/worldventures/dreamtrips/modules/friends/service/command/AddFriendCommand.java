package com.worldventures.dreamtrips.modules.friends.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.circles.AddFriendsToCircleHttpAction;
import com.worldventures.dreamtrips.api.friends.SendFriendRequestHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
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
