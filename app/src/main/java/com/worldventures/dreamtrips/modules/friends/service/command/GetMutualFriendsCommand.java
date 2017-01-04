package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.GetMutualFriendsHttpAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetMutualFriendsCommand extends GetUsersCommand {

   private int userId;

   public GetMutualFriendsCommand(int userId) {
      this.userId = userId;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetMutualFriendsHttpAction.class)
            .createObservableResult(new GetMutualFriendsHttpAction(userId))
            .map(GetMutualFriendsHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_mutual_friends;
   }
}
