package com.worldventures.dreamtrips.modules.friends.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.AnswerFriendRequestsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendRequestParams;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

public abstract class ActOnFriendRequestCommand extends CommandWithError<User> implements InjectableAction {

   protected User user;

   @Inject Janet janet;

   public ActOnFriendRequestCommand(User user) {
      this.user = user;
   }

   @Override
   protected void run(CommandCallback<User> callback) throws Throwable {
      janet.createPipe(AnswerFriendRequestsHttpAction.class)
            .createObservableResult(new AnswerFriendRequestsHttpAction(getRequestParams()))
            .subscribe(action -> {
               callback.onSuccess(user);
            }, callback::onFail);
   }

   abstract FriendRequestParams getRequestParams();

   @CommandAction
   public static final class Accept extends ActOnFriendRequestCommand {

      private String circleId;

      public Accept(User user, String circleId) {
         super(user);
         this.circleId = circleId;
      }

      @Override
      public int getFallbackErrorMessage() {
         return R.string.error_fail_to_accept_friend_request;
      }

      @Override
      FriendRequestParams getRequestParams() {
         return FriendRequestParams.confirm(user.getId(), circleId);
      }
   }

   @CommandAction
   public static final class Reject extends ActOnFriendRequestCommand {

      public Reject(User user) {
         super(user);
      }

      @Override
      public int getFallbackErrorMessage() {
         return R.string.error_fail_to_reject_friend_request;
      }

      @Override
      FriendRequestParams getRequestParams() {
         return FriendRequestParams.reject(user.getId());
      }
   }

}
