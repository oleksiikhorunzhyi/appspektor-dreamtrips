package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.SearchFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.ImmutableSearchParams;
import com.worldventures.dreamtrips.api.friends.model.SearchParams;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetSearchUsersCommand extends GetUsersCommand {

   private final String query;
   private final int page;
   private final int perPage;

   public GetSearchUsersCommand(String query, int page, int perPage) {
      this.query = query;
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(SearchFriendsHttpAction.class)
            .createObservableResult(new SearchFriendsHttpAction(provideSearchParams()))
            .map(SearchFriendsHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SearchParams provideSearchParams() {
      return ImmutableSearchParams.builder()
            .page(page)
            .perPage(perPage)
            .query(query)
            .build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_users;
   }
}
