package com.worldventures.dreamtrips.modules.friends.view.fragment;


import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.DelaySearchView;
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

    private ListPopupWindow popupWindow;

    @OnClick(R.id.global)
    void onGlobalSearchClicked() {
        NavigationBuilder.create().with(activityRouter)
                .data(new FriendGlobalSearchBundle("" + search.getQuery()))
                .move(Route.FRIEND_SEARCH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                onGlobalSearchClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                recyclerView.hideEmptyView();
                getPresenter().setQuery(s);
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
        popupWindow = new ListPopupWindow(getActivity());
        ArrayAdapter<Circle> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_single_choice, circles);
        popupWindow.setAdapter(adapter);
        popupWindow.setAnchorView(filter);
        popupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.filter_popup_width));
        popupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        popupWindow.setModal(true);
        popupWindow.show();

        popupWindow.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        popupWindow.setSelection(position);
        popupWindow.getListView().setOnItemClickListener((adapterView, view, i, l) -> {
            popupWindow.dismiss();
            getPresenter().reloadWithFilter(i);
        });
    }

    @Override
    protected FriendListPresenter createPresenter(Bundle savedInstanceState) {
        return new FriendListPresenter();
    }

}
