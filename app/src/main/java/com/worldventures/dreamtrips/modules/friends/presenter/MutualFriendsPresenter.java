package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.bundle.MutualFriendsBundle;
import com.worldventures.dreamtrips.modules.friends.service.command.GetMutualFriendsCommand;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;

public class MutualFriendsPresenter extends BaseUserListPresenter<MutualFriendsPresenter.View> {

   private int userId;

   public MutualFriendsPresenter(MutualFriendsBundle bundle) {
      userId = bundle.getId();
   }

   @Override
   protected void loadUsers(int page, Action1<List<User>> onSuccessAction) {
      friendsInteractor.getMutualFriendsPipe()
            .createObservable(new GetMutualFriendsCommand(userId))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetMutualFriendsCommand>()
                  .onSuccess(getMutualFriendsCommand -> onSuccessAction.call(getMutualFriendsCommand.getResult()))
                  .onFail((getMutualFriendsCommand, throwable) -> onError(getMutualFriendsCommand)));
   }

   @Override
   public void scrolled(int totalItemCount, int lastVisible) {
      // now api doesn't have pagination therefore lazy loading is disabled
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

   public interface View extends BaseUserListPresenter.View {

   }
}
