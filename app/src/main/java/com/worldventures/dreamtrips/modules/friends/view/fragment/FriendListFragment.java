package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.DelaySearchView;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.friends.bundle.BaseUsersBundle;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_friends)
@MenuResource(R.menu.menu_friend)
public class FriendListFragment extends BaseUsersFragment<FriendListPresenter, BaseUsersBundle>
        implements FriendListPresenter.View {


    @InjectView(R.id.iv_filter)
    ImageView filter;
    @InjectView(R.id.search)
    DelaySearchView search;


    @OnClick(R.id.global)
    void onGlobalSearchClicked() {
        openFriendSearch((adapter.getCount() == 0 && search.getQuery() != null) ? search.getQuery().toString() : "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                openFriendSearch("");
                TrackingHelper.tapFeedButton(TrackingHelper.ATTRIBUTE_ADD_FRIENDS);
                TrackingHelper.tapFeedButton(TrackingHelper.ATTRIBUTE_SEARCH_FRIENDS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFriendSearch(String query){
        router.moveTo(Route.FRIEND_SEARCH, NavigationConfigBuilder.forActivity()
                .data(new FriendGlobalSearchBundle(query))
                .build());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter.registerCell(User.class, FriendCell.class);
        search.setDelayInMillis(500);
        search.setIconifiedByDefault(false);

        search.setQuery(getPresenter().getQuery(), false);
        search.setQueryHint(getString(R.string.friend_search_placeholder));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getPresenter().setQuery(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (recyclerView != null) {
                    recyclerView.hideEmptyView();
                    getPresenter().setQuery(s);
                }
                return false;
            }
        });
    }

    @OnClick(R.id.iv_filter)
    public void onActionFilter() {
        getPresenter().onFilterClicked();
    }

    @Override
    public void showFilters(List<Circle> circles, int position) {
        CirclesFilterPopupWindow filterPopupWindow = new CirclesFilterPopupWindow(getContext());
        filterPopupWindow.setCircles(circles);
        filterPopupWindow.setAnchorView(filter);
        filterPopupWindow.setOnItemClickListener((parent, view, pos, id) -> {
            filterPopupWindow.dismiss();
            getPresenter().reloadWithFilter(pos);
            TrackingHelper.filterMyFriendsFeed(circles.get(pos).getName());
        });
        filterPopupWindow.show();
        filterPopupWindow.setCheckedCircle(circles.get(position));
    }

    @Override
    protected FriendListPresenter createPresenter(Bundle savedInstanceState) {
        return new FriendListPresenter();
    }

}
