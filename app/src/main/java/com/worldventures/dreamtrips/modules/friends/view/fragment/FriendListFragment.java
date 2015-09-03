package com.worldventures.dreamtrips.modules.friends.view.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.badoo.mobile.util.WeakHandler;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.DelaySearchView;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendListPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_friends)
@MenuResource(R.menu.menu_friend)
public class FriendListFragment extends BaseFragment<FriendListPresenter> implements SwipeRefreshLayout.OnRefreshListener, FriendListPresenter.View {


    @InjectView(R.id.iv_filter)
    ImageView filter;
    @InjectView(R.id.search)
    DelaySearchView search;
    @InjectView(R.id.empty)
    RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFriends)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    private RecyclerViewStateDelegate stateDelegate;

    private LoaderRecycleAdapter<User> adapter;
    private ListPopupWindow popupWindow;

    private WeakHandler weakHandler;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                getPresenter().globalSearch();
                NavigationBuilder.create().with(activityRouter).move(Route.FRIEND_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        adapter = new LoaderRecycleAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(User.class, FriendCell.class);

        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        setLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
    }

    private void setLayoutManager() {
        layoutManager = ViewUtils.isLandscapeOrientation(getActivity()) ?
                new GridLayoutManager(getActivity(),
                        ViewUtils.isTablet(getActivity()) ? 3 : 1) :
                new LinearLayoutManager(getActivity());
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
    public BaseArrayListAdapter<User> getAdapter() {
        return adapter;
    }

    @Override
    public void openFriendPrefs(UserBundle userBundle) {
        NavigationBuilder.create().with(activityRouter).data(userBundle).move(Route.FRIEND_PREFERENCES);
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }
}
