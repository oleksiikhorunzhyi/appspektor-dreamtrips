package com.worldventures.dreamtrips.view.fragment;

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
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.DreamTripsFragmentPM;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.utils.events.LikeTripEvent;
import com.worldventures.dreamtrips.utils.events.ResetFiltersEvent;
import com.worldventures.dreamtrips.utils.events.TouchTripEvent;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.view.cell.TripCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dream_trips)
@MenuResource(R.menu.menu_dream_trips)
public class DreamTripsFragment extends BaseFragment<DreamTripsFragmentPM> implements DreamTripsFragmentPM.View, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    @InjectView(R.id.recyclerViewTrips)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    FilterableArrayListAdapter<Trip> adapter;

    private int lastConfig;
    private boolean search;

    @Override
    public void afterCreateView(View rootView) {
        lastConfig = getResources().getConfiguration().orientation;
        super.afterCreateView(rootView);
        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));

        this.recyclerView.setEmptyView(emptyView);

        this.adapter = new FilterableArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.adapter.registerCell(Trip.class, TripCell.class);

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

    private void setupLayoutManager(boolean landscape) {
        int spanCount;
        if (landscape) {
            spanCount = ViewUtils.isTablet(getActivity()) ? 3 : 2;
        } else {
            spanCount = ViewUtils.isTablet(getActivity()) ? 2 : 1;
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
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
        getPresentationModel().onItemLike(likeTripEvent.getTrip());
    }

    public void onEvent(TouchTripEvent event) {
        getPresentationModel().onItemClick(event.getTrip());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (lastConfig != newConfig.orientation) {
            lastConfig = newConfig.orientation;
            setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
                getPresentationModel().actionMap();
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
        super.onDestroyView();
        getPresentationModel().onPause();
    }

    @Override
    public void onRefresh() {
        getPresentationModel().reload();
    }

    @Override
    protected DreamTripsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DreamTripsFragmentPM(this);
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading(List<Trip> items) {
        refreshLayout.post(() -> refreshLayout.setRefreshing(false));
    }

    @Override
    public IRoboSpiceAdapter<Trip> getAdapter() {
        return adapter;
    }
}