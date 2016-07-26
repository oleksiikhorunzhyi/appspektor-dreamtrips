package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.DelaySearchView;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.UserSearchCellDelegate;

import java.util.ArrayList;

@Layout(R.layout.fragment_search_friends)
@MenuResource(R.menu.menu_search)
public class FriendSearchFragment extends BaseUsersFragment<FriendSearchPresenter, FriendGlobalSearchBundle>
        implements FriendSearchPresenter.View, UserSearchCellDelegate {

    private DelaySearchView searchView;

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
        searchView = (DelaySearchView) MenuItemCompat.getActionView(searchItem);
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
                getPresenter().setQuery(newText);
                enableRefreshLayout(!newText.isEmpty());
                return false;
            }
        });

        if (getPresenter().getQuery().length() > 0) {
            searchView.setQuery(getPresenter().getQuery(), true);
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
        getPresenter().addUserRequest(user);
    }

    @Override
    public void onCellClicked(User model) {

    }

    private void enableRefreshLayout(boolean isEnable) {
        refreshLayout.setEnabled(isEnable);
    }

    private String getQueryFromArgs() {
        return getArgs() != null ? getArgs().getQuery() : "";
    }
}
