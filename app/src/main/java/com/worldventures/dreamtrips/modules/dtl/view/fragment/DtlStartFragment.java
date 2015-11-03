package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.api.Status;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlStartPresenter;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Transitional fragment that determines further navigation.<br />
 * Depending on DtlLocation being previously selected it opens Dtl Places List screen (if location was selected) <br />
 * or Dtl Locations screen (if not).
 */
@Layout(R.layout.fragment_dtl_start_empty)
@MenuResource(R.menu.menu_mock)
public class DtlStartFragment extends BaseFragment<DtlStartPresenter> implements DtlStartPresenter.View {

    private static final int REQUEST_CHECK_SETTINGS = 1488;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_LOCATION_PERM = 3;

    @Inject
    ActivityResultDelegate activityResultDelegate;

    @Override
    public void onResume() {
        super.onResume();
        activityResult(activityResultDelegate.getRequestCode(),
                activityResultDelegate.getResultCode(), activityResultDelegate.getData());
        showDtlFilters();
    }

    private void showDtlFilters() {
        router.moveTo(Route.DTL_FILTERS, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .containerId(R.id.container_filters)
                .fragmentManager(getFragmentManager())
                .build());
    }

    @Override
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
            Snackbar.make(getView(), R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> ActivityCompat.requestPermissions(getActivity(), permissions,
                            RC_HANDLE_LOCATION_PERM))
                    .show();
        }
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
    protected DtlStartPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlStartPresenter();
    }

    @Override
    public void openDtlLocationsScreen() {
        router.moveTo(Route.DTL_LOCATIONS, provideNavigationConfig(null));
    }

    @Override
    public void openDtlPlacesScreen(PlacesBundle bundle) {
        router.moveTo(Route.DTL_PLACES_LIST, provideNavigationConfig(bundle));
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

    private NavigationConfig provideNavigationConfig(@Nullable Parcelable bundle) {
        return NavigationConfigBuilder.forFragment()
                .fragmentManager(getChildFragmentManager())
                .backStackEnabled(false)
                .containerId(R.id.dtl_container)
                .data(bundle)
                .build();
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        getPresenter().locationNotGranted();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
