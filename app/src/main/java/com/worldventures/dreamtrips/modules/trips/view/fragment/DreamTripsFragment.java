package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.LikeTripEvent;
import com.worldventures.dreamtrips.core.utils.events.ResetFiltersEvent;
import com.worldventures.dreamtrips.core.utils.events.TouchTripEvent;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.DreamTripsFragmentPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dream_trips)
@MenuResource(R.menu.menu_dream_trips)
public class DreamTripsFragment extends BaseFragment<DreamTripsFragmentPresenter> implements
        DreamTripsFragmentPresenter.View, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    @InjectView(R.id.recyclerViewTrips)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private FilterableArrayListAdapter<TripModel> adapter;

    private int lastConfig;
    private boolean search;

    private SearchView searchView;

    private int lastScrollPosition = 0;

    @Override
    public void afterCreateView(View rootView) {
        lastConfig = getResources().getConfiguration().orientation;
        super.afterCreateView(rootView);
        setupLayoutManager();

        this.recyclerView.setEmptyView(emptyView);

        this.adapter = new FilterableArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.adapter.registerCell(TripModel.class, TripCell.class);

        this.recyclerView.setAdapter(adapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }



    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.setFilter(s);
        return false;
    }

    private void saveScrollPosition() {
        lastScrollPosition = ((GridLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void setupLayoutManager() {
        if (recyclerView.getLayoutManager() != null) {
            saveScrollPosition();
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), getSpanCount());
        this.recyclerView.setLayoutManager(layoutManager);
        layoutManager.scrollToPosition(lastScrollPosition);
    }

    private int getSpanCount() {
        int spanCount;
        if (ViewUtils.isLandscapeOrientation(getActivity())) {
            spanCount = ViewUtils.isTablet(getActivity()) ? 3 : 2;
        } else {
            spanCount = ViewUtils.isTablet(getActivity()) ? 2 : 1;
        }
        return spanCount;
    }

    @Override
    public void dataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showErrorMessage() {
        ((MainActivity) getActivity()).informUser(getString(R.string.smth_went_wrong));
    }

    public void onEvent(LikeTripEvent likeTripEvent) {
        getPresenter().onItemLike(likeTripEvent.getTrip());
    }

    public void onEvent(TouchTripEvent event) {
        getPresenter().onItemClick(event.getTrip());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (lastConfig != newConfig.orientation) {
            lastConfig = newConfig.orientation;
            setupLayoutManager();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(() -> {
            adapter.flushFilter();
            return false;
        });
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                ((MainActivity) getActivity()).openRightDrawer();
                break;
            case R.id.action_map:
                getPresenter().actionMap();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.textViewResetFilters)
    public void resetFilters() {
        getEventBus().post(new ResetFiltersEvent());
    }


    @Override
    public void onDestroyView() {
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    protected DreamTripsFragmentPresenter createPresenter(Bundle savedInstanceState) {
        return new DreamTripsFragmentPresenter(this);
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading(List<TripModel> items) {
        refreshLayout.post(() -> refreshLayout.setRefreshing(false));
    }

    @Override
    public IRoboSpiceAdapter<TripModel> getAdapter() {
        return adapter;
    }

    @Override
    public void clearSearch() {
        if (searchView != null) {
            searchView.setQuery("", true);
            searchView.clearFocus();
        }
    }
}