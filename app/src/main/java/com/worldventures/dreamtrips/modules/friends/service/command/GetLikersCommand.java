package com.worldventures.dreamtrips.modules.friends.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.likes.GetLikersHttpAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetLikersCommand extends GetUsersCommand {

   private FeedEntity feedEntity;
   private int page;
   private int perPage;

   @Inject SessionHolder<UserSession> sessionHolder;

   public GetLikersCommand(FeedEntity feedEntity, int page, int perPage) {
      this.feedEntity = feedEntity;
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetLikersHttpAction.class)
            .createObservableResult(new GetLikersHttpAction(feedEntity.getUid(), page, perPage))
            .map(GetLikersHttpAction::response)
            .map(this::convert)
            .doOnNext(this::onLikersLoaded)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void onLikersLoaded(List<User> users) {
      if (users == null || users.isEmpty()) {
         feedEntity.setFirstLikerName(null);
      } else {
         User userWhoLiked = Queryable.from(users)
               .firstOrDefault(user -> user.getId() != sessionHolder.get().get().getUser().getId());
         feedEntity.setFirstLikerName(userWhoLiked != null ? userWhoLiked.getFullName() : null);
      }
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_people_who_liked;
   }
}
