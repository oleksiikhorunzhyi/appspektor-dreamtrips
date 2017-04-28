package com.worldventures.dreamtrips.modules.friends.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.AcceptAllFriendRequestsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class AcceptAllFriendRequestsCommand extends CommandWithError<Void> implements InjectableAction {

   private String circleId;

   @Inject Janet janet;

   public AcceptAllFriendRequestsCommand(String circleId) {
      this.circleId = circleId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) {
      janet.createPipe(AcceptAllFriendRequestsHttpAction.class, Schedulers.io())
            .createObservableResult(new AcceptAllFriendRequestsHttpAction(circleId))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_accept_friend_request;
   }

}
