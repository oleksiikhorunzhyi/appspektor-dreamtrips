package com.worldventures.dreamtrips.social.ui.friends.presenter;

import android.support.annotation.StringRes;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetSearchUsersCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetUsersCommand;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;
import rx.functions.Action1;

public class FriendSearchPresenter extends BaseUserListPresenter<FriendSearchPresenter.View> {

   @State String query;
   private Subscription subscription;

   public FriendSearchPresenter(String query) {
      this.query = query;
   }

   @Override
   protected void loadUsers(int page, Action1<List<User>> onSuccessAction) {
      //It's correcting pagination and removing layering of queries
      if (subscription != null && !subscription.isUnsubscribed()) {
         subscription.unsubscribe();
      }

      subscription = friendsInteractor.getSearchUsersPipe()
            .createObservable(new GetSearchUsersCommand(query, page, getPerPageCount()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetSearchUsersCommand>()
                  .onSuccess(searchUsersCommand -> onSuccessAction.call(new ArrayList<>(searchUsersCommand.getResult())))
                  .onFail(this::onError));
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
            if (view != null) {
               view.updateEmptyCaption(R.string.start_searching);
            }
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
   protected void onError(GetUsersCommand getUsersCommand, Throwable throwable) {
      super.onError(getUsersCommand, throwable);
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
      if (query.length() < 3) {
         return;
      }
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
