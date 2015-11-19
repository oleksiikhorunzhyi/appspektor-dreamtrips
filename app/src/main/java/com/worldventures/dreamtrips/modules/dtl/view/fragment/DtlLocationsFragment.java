package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_locations)
@MenuResource(R.menu.menu_locations)
public class DtlLocationsFragment extends RxBaseFragment<DtlLocationsPresenter> implements DtlLocationsPresenter.View {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    BaseArrayListAdapter adapter;

    @InjectView(R.id.locationsList)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.empty_view)
    View emptyView;
    @InjectView(R.id.progress_text)
    TextView progressText;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.progress)
    View progress;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(emptyView);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources()
                .getDrawable(R.drawable.list_divider), true));

        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider.get());

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
        configureSearch(toolbar.getMenu());
    }

    private void configureSearch(Menu menu) {
        searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    getPresenter().searchOpened();
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    getPresenter().searchClosed();
                    return true;
                }
            });
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(getString(R.string.dtl_locations_search_caption));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getPresenter().search(newText.toLowerCase());
                    return false;
                }
            });
        }

    }

    public void onEvent(LocationClickedEvent event) {
        tryHideSoftInput();
        getPresenter().onLocationSelected(event.getLocation());
    }

    @Override
    public void startLoading() {
        progress.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void finishLoading() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void citiesLoadingStarted() {
        progressText.setText(R.string.dtl_wait_for_cities);
    }

    @Override
    public void setItems(List<DtlLocation> dtlLocations) {
        adapter.clear();
        adapter.addItems(dtlLocations);
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
    public void showMerchants(PlacesBundle bundle) {
        router.moveTo(Route.DTL_PLACES_HOLDER, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_container)
                .fragmentManager(getFragmentManager())
                .backStackEnabled(false)
                .clearBackStack(true)
                .data(bundle)
                .build());
    }

    @Override
    public void showSearch() {
        if (searchItem != null) {
            MenuItemCompat.expandActionView(searchItem);
            searchView.setIconified(false);
        }
    }
}
