package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.friends.service.command.GetLikersCommand;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;

public class UsersLikedItemPresenter extends BaseUserListPresenter<BaseUserListPresenter.View> {

   private FeedEntity feedEntity;

   public UsersLikedItemPresenter(UsersLikedEntityBundle bundle) {
      feedEntity = bundle.getFeedEntity();
   }

   @Override
   protected void loadUsers(int page, Action1<List<User>> onSuccessAction) {
      friendsInteractor.getLikersPipe()
            .createObservable(new GetLikersCommand(feedEntity, page, getPerPageCount()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetLikersCommand>()
                  .onSuccess(likersCommand -> onSuccessAction.call(likersCommand.getResult()))
                  .onFail(this::onError));

   }

   @Override
   protected void userStateChanged(User user) {
      view.finishLoading();

      int index = users.indexOf(user);
      if (index != -1) {
         users.remove(index);
         users.add(index, user);
         view.refreshUsers(users);
      }
   }

   public void acceptRequest(User user) {
      super.acceptRequest(user);
   }

   public void addUserRequest(User user) {
      addFriend(user);
   }
}
