package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.DelaySearchView;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendSearchPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserSearchCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;


@Layout(R.layout.activity_search_friends)
@MenuResource(R.menu.menu_search)
public class FriendSearchFragment extends BaseFragment<FriendSearchPresenter>
        implements FriendSearchPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.empty)
    RelativeLayout emptyView;
    @InjectView(R.id.recyclerViewFriends)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    DelaySearchView searchView;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    private RecyclerViewStateDelegate stateDelegate;

    private LoaderRecycleAdapter<User> adapter;
    private LinearLayoutManager layoutManager;

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
        adapter = new LoaderRecycleAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(User.class, UserSearchCell.class);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (DelaySearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setDelayInMillis(500);
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
                return false;
            }
        });


        searchView.setQuery(getPresenter().getQuery(), true);

    }

    @Override
    protected FriendSearchPresenter createPresenter(Bundle savedInstanceState) {
        return new FriendSearchPresenter();
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
    public void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectedAction) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getString(R.string.profile_add_friend))
                .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles),
                        (materialDialog, view, i, charSequence) -> {
                            selectedAction.apply(i);
                            materialDialog.dismiss();
                        })
                .negativeText(R.string.cancel)
                .show();

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
    public void onDestroy() {
        super.onDestroy();
        stateDelegate.onDestroyView();
    }

}