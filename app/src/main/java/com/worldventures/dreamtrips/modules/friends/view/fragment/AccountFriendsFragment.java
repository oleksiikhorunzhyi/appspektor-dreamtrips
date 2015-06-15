package com.worldventures.dreamtrips.modules.friends.view.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.friends.model.UserWrapper;
import com.worldventures.dreamtrips.modules.friends.presenter.AccountFriendsPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserWrapperCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_account_friends)
public class AccountFriendsFragment extends BaseFragment<AccountFriendsPresenter> implements SwipeRefreshLayout.OnRefreshListener, AccountFriendsPresenter.View {


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

    private FilterableArrayListAdapter<UserWrapper> adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        this.adapter = new FilterableArrayListAdapter<>(getActivity(), injectorProvider);
        this.adapter.registerCell(UserWrapper.class, UserWrapperCell.class);
        this.adapter.setHasStableIds(true);
        this.recyclerView.setEmptyView(emptyView);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    protected AccountFriendsPresenter createPresenter(Bundle savedInstanceState) {
        return new AccountFriendsPresenter();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public IRoboSpiceAdapter<UserWrapper> getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading(List<UserWrapper> items) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void startLoading() {
        refreshLayout.setRefreshing(true);
    }
}
