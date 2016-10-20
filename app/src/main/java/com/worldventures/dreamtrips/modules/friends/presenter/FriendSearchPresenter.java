package com.worldventures.dreamtrips.modules.friends.presenter;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.service.command.GetSearchUsersCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetUsersCommand;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;

public class FriendSearchPresenter extends BaseUserListPresenter<FriendSearchPresenter.View> {

   @State String query;

   public FriendSearchPresenter(String query) {
      this.query = query;
   }

   @Override
   protected void loadUsers(int page, Action1<List<User>> onSuccessAction) {
      friendsInteractor.getSearchUsersPipe()
            .createObservable(new GetSearchUsersCommand(query, page, getPerPageCount()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetSearchUsersCommand>()
                  .onSuccess(searchUsersCommand -> onSuccessAction.call(searchUsersCommand.getResult()))
                  .onFail((searchUsersCommand, throwable) -> onError(searchUsersCommand)));
   }

   @Override
   protected void userStateChanged(User user) {
      view.finishLoading();
      view.refreshUsers(users);
   }

   public void setQuery(String query) {
      this.query = query;
      if (query.length() < 3) {
         if (users.size() > 0) {
            onUsersLoaded(new ArrayList<>());
            if (view != null) view.updateEmptyCaption(R.string.start_searching);
         }
         return;
      }
      reload();
   }

   @Override
   public void reload() {
      super.reload();
      updateEmptyView(true);
   }

   @Override
   protected void onUsersAdded(List<User> freshUsers) {
      super.onUsersAdded(freshUsers);
      updateEmptyView(false);
   }

   @Override
   protected void onUsersLoaded(List<User> freshUsers) {
      super.onUsersLoaded(freshUsers);
      updateEmptyView(false);
   }

   @Override
   protected void onError(GetUsersCommand getUsersCommand) {
      super.onError(getUsersCommand);
      updateEmptyView(false);
   }

   private void updateEmptyView(boolean isLoading) {
      view.updateEmptyView(users.size(), isLoading);
   }

   public void addUserRequest(User user) {
      addFriend(user);
   }

   @Override
   protected boolean isNeedPreload() {
      return false;
   }

   @Override
   public void scrolled(int totalItemCount, int lastVisible) {
      if (query.length() < 3) return;
      super.scrolled(totalItemCount, lastVisible);
   }

   public String getQuery() {
      return query;
   }

   public interface View extends BaseUserListPresenter.View {

      void updateEmptyView(int friendsSize, boolean isLoading);

      void updateEmptyCaption(@StringRes int resource);
   }
}
