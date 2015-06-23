package com.worldventures.dreamtrips.modules.friends.view.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.view.adapter.FilterPopupAdapter;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_friends)
public class FriendListFragment extends BaseFragment<FriendListPresenter> implements SwipeRefreshLayout.OnRefreshListener, FriendListPresenter.View {


    @InjectView(R.id.iv_filter)
    ImageView filter;
    @InjectView(R.id.search)
    SearchView search;
    @InjectView(R.id.empty)
    RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFriends)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    WeakHandler handler = new WeakHandler();
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    private RecyclerViewStateDelegate stateDelegate;

    private LoaderRecycleAdapter<Friend> adapter;
    private ListPopupWindow popupWindow;

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

    @OnClick(R.id.global)
    void onGlobalSearchClicked() {
        getPresenter().globalSearch();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        adapter = new LoaderRecycleAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(Friend.class, FriendCell.class);
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
                getPresenter().setQuery(s);
                return false;
            }
        });
        search.setIconifiedByDefault(true);
    }

    @OnClick(R.id.iv_filter)
    public void onActionFilter() {
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
        else
            getPresenter().onFilterClicked();
    }

    @Override
    public void showFilters(List<Circle> circles) {
        popupWindow = new ListPopupWindow(getActivity());
        popupWindow.setAdapter(new FilterPopupAdapter<>(getActivity(), circles));
        popupWindow.setAnchorView(filter);
        popupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.filter_popup_width));
        popupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        popupWindow.setOnItemClickListener((adapterView, view, position, id) -> {

        });
        popupWindow.show();
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
        handler.post(() -> {
            if (refreshLayout != null)
                refreshLayout.setRefreshing(true);
        });
    }
}
