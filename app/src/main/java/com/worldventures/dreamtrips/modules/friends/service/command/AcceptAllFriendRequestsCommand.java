package com.worldventures.dreamtrips.modules.friends.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.AnswerFriendRequestsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.FriendRequestParams;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AcceptAllFriendRequestsCommand extends CommandWithError<List<User>> implements InjectableAction {

   private List<User> users;
   private String cirlceId;

   @Named(JanetModule.JANET_API_LIB) @Inject Janet janet;

   public AcceptAllFriendRequestsCommand(List<User> users, String cirlceId) {
      this.users = users;
      this.cirlceId = cirlceId;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(AnswerFriendRequestsHttpAction.class)
            .createObservableResult(new AnswerFriendRequestsHttpAction(getRequestBody()))
            .subscribe(action -> {
               callback.onSuccess(users);
            }, callback::onFail);
   }

   private List<FriendRequestParams> getRequestBody() {
      return Queryable.from(users).map(element -> FriendRequestParams.confirm(element.getId(), cirlceId)).toList();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_accept_friend_request;
   }

}
