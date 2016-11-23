package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.GetFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendsParams;
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendsParams;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetFriendsCommand extends GetUsersCommand {

   private Circle circle;
   private String query;
   private int page;
   private int perPage;

   public GetFriendsCommand(Circle circle, String query, int page, int perPage) {
      this.circle = circle;
      this.query = query;
      this.page = page;
      this.perPage = perPage;
   }

   public GetFriendsCommand(Circle circle, int page, int perPage) {
      this(circle, null, page, perPage);
   }

   public GetFriendsCommand(String query, int page, int perPage) {
      this(null, query, page, perPage);
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetFriendsHttpAction.class)
            .createObservableResult(new GetFriendsHttpAction(provideFriendsParams()))
            .map(GetFriendsHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private FriendsParams provideFriendsParams() {
      ImmutableFriendsParams.Builder builder = ImmutableFriendsParams.builder()
            .page(page)
            .perPage(perPage)
            .query(query);

      if (circle != null) builder.circleId(circle.getId());

      return builder.build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_friends;
   }
}
