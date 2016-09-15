package com.worldventures.dreamtrips.modules.friends.presenter;

import android.support.annotation.StringRes;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.SearchUsersQuery;

import java.util.ArrayList;

import icepick.State;

public class FriendSearchPresenter extends BaseUserListPresenter<FriendSearchPresenter.View> {

   @State String query;

   public FriendSearchPresenter(String query) {
      this.query = query;
   }

   @Override
   protected Query<ArrayList<User>> getUserListQuery(int page) {
      return new SearchUsersQuery(query, page, getPerPageCount());
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
   protected void onUsersAdded(ArrayList<User> freshUsers) {
      super.onUsersAdded(freshUsers);
      updateEmptyView(false);
   }

   @Override
   protected void onUsersLoaded(ArrayList<User> freshUsers) {
      super.onUsersLoaded(freshUsers);
      updateEmptyView(false);
   }

   @Override
   public void handleError(SpiceException error) {
      super.handleError(error);
      updateEmptyView(false);
   }

   private void updateEmptyView(boolean isLoading) {
      if (view != null) view.updateEmptyView(users.size(), isLoading);
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
