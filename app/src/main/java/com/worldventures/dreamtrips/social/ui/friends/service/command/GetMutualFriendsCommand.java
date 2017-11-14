package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.GetMutualFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.ImmutableMutualFriendsParams;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetMutualFriendsCommand extends GetUsersCommand {

   private final int userId;
   private final int page;
   private final int perPage;

   public GetMutualFriendsCommand(int userId, int page, int perPage) {
      this.userId = userId;
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetMutualFriendsHttpAction.class)
            .createObservableResult(
                  new GetMutualFriendsHttpAction(ImmutableMutualFriendsParams.builder()
                        .userId(userId).page(page).perPage(perPage).build()))
            .map(GetMutualFriendsHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_mutual_friends;
   }
}
