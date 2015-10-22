package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_locations)
@MenuResource(R.menu.menu_locations)
public class DtlLocationsFragment extends BaseFragment<DtlLocationsPresenter> implements DtlLocationsPresenter.View {

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
    @InjectView(R.id.progress)
    View progress;

    @Override
    protected DtlLocationsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlLocationsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(emptyView);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources()
                .getDrawable(R.drawable.list_divider), true));

        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider.get());

        adapter.registerCell(DtlLocation.class, DtlLocationCell.class);
        adapter.registerCell(String.class, DtlHeaderCell.class);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(getString(R.string.dtl_locations_search_caption));
            searchView.setOnCloseListener(() -> {
                getPresenter().flushSearch();
                return false;
            });
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
        getPresenter().onLocationClicked(event.getLocation());
    }

    @Override
    public void startLoading() {
        progress.setVisibility(View.VISIBLE);
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
    public void setItems(DtlLocationsHolder dtlLocationsHolder) {
        adapter.clear();
        if (!dtlLocationsHolder.getNearby().isEmpty()) {
            adapter.addItem(getString(R.string.dtl_locations_select_nearby_cities));
            adapter.addItems(dtlLocationsHolder.getNearby());
        }
        if (!dtlLocationsHolder.getLocations().isEmpty()) {
            adapter.addItem(getString(R.string.dtl_locations_select_popular));
            adapter.addItems(dtlLocationsHolder.getLocations());
        }
    }

    @Override
    public void openLocation(PlacesBundle bundle) {
        fragmentCompass.setContainerId(R.id.dtl_container);
        fragmentCompass.setFragmentManager(getFragmentManager());

        fragmentCompass.disableBackStack();

        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(bundle)
                .move(Route.DTL_PLACES_LIST);
    }
}
