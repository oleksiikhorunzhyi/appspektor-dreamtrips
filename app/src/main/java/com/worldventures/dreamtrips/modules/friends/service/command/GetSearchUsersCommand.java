package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.SearchFriendsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.ImmutableSearchParams;
import com.worldventures.dreamtrips.api.friends.model.SearchParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetSearchUsersCommand extends GetUsersCommand {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private String query;
   private int page;
   private int perPage;

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
