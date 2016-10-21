package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.GetFriendRequestsHttpAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetRequestsCommand extends GetUsersCommand {

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetFriendRequestsHttpAction.class)
            .createObservableResult(new GetFriendRequestsHttpAction())
            .map(GetFriendRequestsHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_friend_requests;
   }
}


