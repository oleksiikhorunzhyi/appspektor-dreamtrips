package com.worldventures.dreamtrips.modules.friends.view.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.activity_search_friends)
@MenuResource(R.menu.menu_search)
public class FriendSearchActivity extends ActivityWithPresenter<FriendSearchPresenter>
        implements FriendSearchPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.empty)
    RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFriends)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    private RecyclerViewStateDelegate stateDelegate;

    private LoaderRecycleAdapter<User> adapter;

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
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        stateDelegate.setRecyclerView(recyclerView);
        adapter = new LoaderRecycleAdapter<>(this, injectorProvider);
        adapter.registerCell(User.class, UserSearchCell.class);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconified(false);
        searchItem.expandActionView();
        searchView.setOnCloseListener(() -> {
            getPresentationModel().reload();
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getPresentationModel().setQuery(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    protected FriendSearchPresenter createPresentationModel(Bundle savedInstanceState) {
        return new FriendSearchPresenter();
    }

    @Override
    public void onRefresh() {
        getPresentationModel().reload();
    }

    @Override
    public BaseArrayListAdapter<User> getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void startLoading() {
        if (refreshLayout != null)
            refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void showCirclePopup() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stateDelegate.onDestroyView();
    }

}
