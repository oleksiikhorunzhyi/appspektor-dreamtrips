package com.worldventures.dreamtrips.social.ui.friends.view.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.DelaySearchView;
import com.worldventures.dreamtrips.social.ui.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.UserSearchCellDelegate;

import java.util.ArrayList;

@Layout(R.layout.fragment_search_friends)
@MenuResource(R.menu.menu_search)
public class FriendSearchFragment extends BaseUsersFragment<FriendSearchPresenter, FriendGlobalSearchBundle>
      implements FriendSearchPresenter.View, UserSearchCellDelegate {

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter.registerCell(User.class, UserSearchCell.class);
      adapter.registerDelegate(User.class, this);
   }

   @Override
   public void onResume() {
      super.onResume();
      enableRefreshLayout(!getQueryFromArgs().isEmpty());
   }

   @Override
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      MenuItem searchItem = menu.findItem(R.id.action_search);
      DelaySearchView searchView = (DelaySearchView) MenuItemCompat.getActionView(searchItem);
      searchView.setIconified(false);
      searchView.setIconifiedByDefault(false);
      searchView.setDelayInMillis(500);
      searchView.setQueryHint(getString(R.string.search));
      searchItem.expandActionView();

      MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
         @Override
         public boolean onMenuItemActionExpand(MenuItem item) {
            return false;
         }

         @Override
         public boolean onMenuItemActionCollapse(MenuItem item) {
            getActivity().onBackPressed();
            return false;
         }
      });

      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
            return false;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
            getPresenter().search(newText);
            enableRefreshLayout(!newText.isEmpty());
            return false;
         }
      });

      if (getPresenter().query.length() > 0) {
         searchView.setQuery(getPresenter().query, true);
      } else {
         adapter.addItems(new ArrayList<>());
      }
   }

   @Override
   protected FriendSearchPresenter createPresenter(Bundle savedInstanceState) {
      return new FriendSearchPresenter(getQueryFromArgs());
   }

   @Override
   public void updateEmptyView(int friendsSize, boolean isLoading) {
      if (emptyView == null) {
         return; // rare NPE fix
      }
      if (isLoading) {
         emptyView.setVisibility(View.GONE);
      } else {
         if (friendsSize == 0) {
            emptyView.setVisibility(View.VISIBLE);
            updateEmptyCaption(R.string.filter_no_results);
         } else {
            emptyView.setVisibility(View.GONE);
         }
      }
   }

   public void updateEmptyCaption(@StringRes int resource) {
      caption.setText(resource);
   }

   @Override
   public void addUserRequest(User user) {
      getPresenter().addUserRequest(user.copy());
   }

   private void enableRefreshLayout(boolean isEnable) {
      refreshLayout.setEnabled(isEnable);
   }

   private String getQueryFromArgs() {
      return getArgs() != null ? getArgs().getQuery() : "";
   }
}
