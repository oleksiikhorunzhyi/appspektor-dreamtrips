package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_bucket_popular)
@MenuResource(R.menu.menu_bucket_popular)
public class BucketListPopuralFragment extends BaseFragment<BucketPopularPresenter>
        implements BucketPopularPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.recyclerViewBuckets)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected View emptyView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private FilterableArrayListAdapter<PopularBucketItem> adapter;
    RecyclerViewStateDelegate stateDelegate;

    @Override
    protected BucketPopularPresenter createPresenter(Bundle savedInstanceState) {
        BucketTabsPresenter.BucketType type = (BucketTabsPresenter.BucketType) getArguments().getSerializable(BucketActivity.EXTRA_TYPE);
        return new BucketPopularPresenter(type);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        this.recyclerView.setLayoutManager(getLayoutManager());
        this.recyclerView.setEmptyView(emptyView);
        this.adapter = new FilterableArrayListAdapter<>(getActivity(), injectorProvider);
        this.adapter.registerCell(PopularBucketItem.class, BucketPopularCell.class);
        this.recyclerView.setAdapter(this.adapter);
        stateDelegate.setRecyclerView(recyclerView);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        RecyclerView.LayoutManager layoutManager;
        if (isTabletLandscape()) {
            return new GridLayoutManager(getActivity(), 3);
        } else {
            return new LinearLayoutManager(getActivity());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(() -> {
            getPresenter().searchClosed();
            return false;
        });
        searchView.setOnQueryTextListener(onQueryTextListener);
    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            getPresenter().onSearch(newText);
            adapter.setFilter(newText);
            return false;
        }
    };

    @Override
    public FilterableArrayListAdapter<PopularBucketItem> getAdapter() {
        return adapter;
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

}