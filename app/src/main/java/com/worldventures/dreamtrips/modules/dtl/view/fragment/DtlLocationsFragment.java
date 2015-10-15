package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

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
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlHeaderCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_locations)
@MenuResource(R.menu.menu_mock)
public class DtlLocationsFragment extends BaseFragment<DtlLocationsPresenter> implements DtlLocationsPresenter.View {

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

    @InjectView(R.id.progressBarImage)
    ProgressBar progressBar;

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
            Snackbar.make(recyclerView, R.string.permission_camera_rationale,
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setItems(List<DtlLocation> dtlLocations) {
        adapter.setItems(dtlLocations);
        adapter.addItem(0, getString(R.string.dtl_locations_select_nearby_cities));
    }

    @Override
    public void openLocation(PlacesBundle bundle) {
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
