package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ResetFiltersEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripListPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

@Layout(R.layout.fragment_trip_list)
@MenuResource(R.menu.menu_dream_trips)
public class TripListFragment extends RxBaseFragment<TripListPresenter> implements
        TripListPresenter.View, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    @InjectView(R.id.recyclerViewTrips)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private BaseArrayListAdapter<TripModel> adapter;

    private SearchView searchView;
    RecyclerViewStateDelegate stateDelegate;

    private WeakHandler weakHandler;

    @State
    boolean searchOpened;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        GridLayoutManager layout = new GridLayoutManager(getActivity(), getSpanCount());
        recyclerView.setLayoutManager(layout);
        recyclerView.setEmptyView(emptyView);

        adapter = new FilterableArrayListAdapter<>(getActivity(), this);
        adapter.registerCell(TripModel.class, TripCell.class);

        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        showFilters();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = layout.findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
    }

    private void showFilters() {
        Fragment filtersFragment = getFragmentManager().findFragmentById(R.id.container_filters);
        if (filtersFragment != null && filtersFragment.getClass().getName()
                .equals(Route.TRIP_FILTERS.getClazzName())) return;

        router.moveTo(Route.TRIP_FILTERS, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .containerId(R.id.container_filters)
                .fragmentManager(getFragmentManager())
                .build());
    }


    @Override
    public void onResume() {
        super.onResume();
        TrackingHelper.viewDreamTripsScreen();
    }

    @Override
    protected void restoreState(Bundle savedInstanceState) {
        super.restoreState(savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        getPresenter().search(s);
        return false;
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

    @Override
    protected void onMenuInflated(Menu menu) {
        super.onMenuInflated(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            if (searchOpened) searchItem.expandActionView();
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    searchOpened = true;
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    searchOpened = false;
                    return true;
                }
            });
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(getString(R.string.search_trips));
            searchView.setQuery(getPresenter().getQuery(), false);
            searchView.setOnCloseListener(() -> {
                getPresenter().search("");
                return false;
            });
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                ((MainActivity) getActivity()).openRightDrawer();
                break;
            case R.id.action_map:
                actionMap();
                TrackingHelper.tapDreamTripsButton(TrackingHelper.ATTRIBUTE_MAP);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.textViewResetFilters)
    public void resetFilters() {
        getEventBus().post(new ResetFiltersEvent());
        clearSearch();
    }


    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    protected TripListPresenter createPresenter(Bundle savedInstanceState) {
        return new TripListPresenter();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public IRoboSpiceAdapter<TripModel> getAdapter() {
        return adapter;
    }

    @Override
    public void itemLiked(FeedEntity feedEntity) {
        TripModel trip = Queryable.from(adapter.getItems()).firstOrDefault(element -> element.getUid().equals(feedEntity.getUid()));
        if (trip != null) {
            trip.syncLikeState(feedEntity);
            dataSetChanged();
            if (isVisibleOnScreen()) {
                new SweetDialogHelper().notifyTripLiked(getActivity(), trip);
            }
        }

    }

    @Override
    public boolean isSearchOpened() {
        return searchOpened;
    }

    public void clearSearch() {
        if (searchView != null) {
            searchView.setQuery("", true);
            searchView.clearFocus();
        }
    }

    private void actionMap() {
        router.moveTo(Route.MAP, NavigationConfigBuilder.forFragment()
                .fragmentManager(getFragmentManager())
                .containerId(R.id.container_main)
                .backStackEnabled(true)
                .build());
    }
}