package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.event.LocationClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_locations)
@MenuResource(R.menu.menu_locations)
public class DtlLocationsFragment extends RxBaseFragment<DtlLocationsPresenter>
        implements DtlLocationsPresenter.View {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    //
    BaseArrayListAdapter adapter;
    //
    @InjectView(R.id.locationsList)
    RecyclerView recyclerView;
    @InjectView(R.id.empty_view)
    View emptyView;
    @InjectView(R.id.obtaining_gps_location_progress_caption)
    TextView gpsProgressCaption;
    @InjectView(R.id.obtaining_locations_progress_caption)
    TextView locationsProgressCaption;
    @InjectView(R.id.progress)
    View progressView;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    //
    SearchView searchView;
    MenuItem searchItem;

    @Override
    protected DtlLocationsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlLocationsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        initToolbar();
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources()
                .getDrawable(R.drawable.list_divider), true));
        //
        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider.get());
        //
        adapter.registerCell(DtlLocation.class, DtlLocationCell.class);
        adapter.registerCell(String.class, DtlHeaderCell.class);
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar() {
        toolbar.setTitle(Route.DTL_LOCATIONS.getTitleRes());
        toolbar.inflateMenu(R.menu.menu_locations);
        if (!tabletAnalytic.isTabletLandscape())
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
        toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
    }

    private void configureSearch() {
        searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            MenuItemCompat.setOnActionExpandListener(searchItem, searchViewExpandListener);
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(getString(R.string.dtl_locations_search_caption));
            searchView.setOnQueryTextListener(searchViewQueryListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        configureSearch();
    }

    @Override
    public void onPause() {
        MenuItemCompat.setOnActionExpandListener(searchItem, null);
        searchView.setOnQueryTextListener(null);
        super.onPause();
    }

    public void onEvent(LocationClickedEvent event) {
        tryHideSoftInput();
        getPresenter().onLocationSelected(event.getLocation());
    }

    @Override
    public void setItems(List<DtlLocation> dtlLocations) {
        emptyView.setVisibility(View.GONE);
        adapter.clear();
        adapter.addItems(dtlLocations);
    }

    @Override
    public void showGpsObtainingProgress() {
        progressView.setVisibility(View.VISIBLE);
        gpsProgressCaption.setVisibility(View.VISIBLE);
        locationsProgressCaption.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showLocationsObtainingProgress() {
        progressView.setVisibility(View.VISIBLE);
        gpsProgressCaption.setVisibility(View.GONE);
        locationsProgressCaption.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyProgress() {
        progressView.setVisibility(View.VISIBLE);
        gpsProgressCaption.setVisibility(View.GONE);
        locationsProgressCaption.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
        progressView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        if (searchView != null) {
            searchView.setOnCloseListener(null);
            searchView.setOnQueryTextListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public void navigateToMerchants() {
        router.moveTo(Route.DTL_MERCHANTS_HOLDER, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_container)
                .fragmentManager(getFragmentManager())
                .backStackEnabled(false)
                .clearBackStack(true)
                .build());
    }

    @Override
    public void showSearch() {
        if (searchItem != null) {
            MenuItemCompat.expandActionView(searchItem);
            searchView.setIconified(false);
            hideProgress();
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private MenuItemCompat.OnActionExpandListener searchViewExpandListener =
            new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    getPresenter().searchOpened();
                    return true;
                }
                //
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    getPresenter().searchClosed();
                    return true;
                }
            };

    private SearchView.OnQueryTextListener searchViewQueryListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                //
                @Override
                public boolean onQueryTextChange(String newText) {
                    getPresenter().search(newText.toLowerCase());
                    return false;
                }
            };
}
