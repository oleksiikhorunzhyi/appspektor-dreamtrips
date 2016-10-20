package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.GetMutualFriendsHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetMutualFriendsCommand extends GetUsersCommand {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

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
