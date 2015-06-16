package com.worldventures.dreamtrips.modules.friends.view.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.friends.model.Friend;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserWrapperCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter.FilterType;

@Layout(R.layout.fragment_account_friends)
public class FriendListFragment extends BaseFragment<FriendListPresenter> implements SwipeRefreshLayout.OnRefreshListener, FriendListPresenter.View {


    @InjectView(R.id.iv_filter)
    ImageView ivFilter;
    @InjectView(R.id.search)
    SearchView search;
    @InjectView(R.id.ll_empty_view)
    RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFriends)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    private RecyclerViewStateDelegate stateDelegate;

    private FilterableArrayListAdapter<Friend> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        adapter = new FilterableArrayListAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(Friend.class, UserWrapperCell.class);
        adapter.setHasStableIds(true);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.setFilter(s);
                return false;
            }
        });
        search.setIconifiedByDefault(true);
    }

    @OnClick(R.id.iv_filter)
    public void onActionFilter() {
        View menuItemView = getActivity().findViewById(R.id.iv_filter);
        PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
        popupMenu.inflate(R.menu.menu_friend_list_filter);
        FilterType filterType = getPresenter().getFilterType();
        popupMenu.getMenu().getItem(filterType.ordinal()).setChecked(true);
        popupMenu.setOnMenuItemClickListener((menuItem) -> {
            getPresenter().reloadWithFilter(menuItem.getItemId());
            return false;
        });
        popupMenu.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stateDelegate.onDestroyView();
    }

    @Override
    protected FriendListPresenter createPresenter(Bundle savedInstanceState) {
        return new FriendListPresenter();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public IRoboSpiceAdapter<Friend> getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading(List<Friend> items) {
        refreshLayout.setRefreshing(false);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void startLoading() {
        if (refreshLayout != null)
            refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }
}
