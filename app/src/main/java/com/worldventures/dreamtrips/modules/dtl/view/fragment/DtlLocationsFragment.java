package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
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
import timber.log.Timber;

@Layout(R.layout.fragment_dtl_locations)
@MenuResource(R.menu.menu_locations)
public class DtlLocationsFragment extends BaseFragment<DtlLocationsPresenter> implements DtlLocationsPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 0;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_LOCATION_PERM = 3;

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

        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider);

        adapter.registerCell(DtlLocation.class, DtlLocationCell.class);
        adapter.registerCell(String.class, DtlHeaderCell.class);
        recyclerView.setAdapter(adapter);

        checkPermissions();
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

    public void checkPermissions() {
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            getPresenter().permissionGranted();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_LOCATION_PERM);
        } else {
            Snackbar.make(recyclerView, R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> ActivityCompat.requestPermissions(getActivity(), permissions,
                            RC_HANDLE_LOCATION_PERM))
                    .show();
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
    public void resolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening settings activity.");
        }
    }

    @Override
    public void setItems(DtlLocationsHolder dtlLocationsHolder) {
        adapter.clear();
        if (!dtlLocationsHolder.getNearby().isEmpty()) {
            adapter.addItem(getString(R.string.dtl_locations_select_nearby_cities));
            adapter.addItems(dtlLocationsHolder.getNearby());
        }
        if (!dtlLocationsHolder.getCities().isEmpty()) {
            adapter.addItem(getString(R.string.dtl_locations_select_popular));
            adapter.addItems(dtlLocationsHolder.getCities());
        }
    }

    @Override
    public void openLocation(PlacesBundle bundle) {
        fragmentCompass.setContainerId(R.id.dtl_container);
        fragmentCompass.setSupportFragmentManager(getFragmentManager());

        fragmentCompass.remove(Route.DTL_PLACES_LIST);

        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(bundle)
                .move(Route.DTL_PLACES_LIST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_LOCATION_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPresenter().permissionGranted();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_location_permission)
                .setPositiveButton(R.string.ok, (dialog, id) -> getActivity().finish())
                .show();
    }

}
