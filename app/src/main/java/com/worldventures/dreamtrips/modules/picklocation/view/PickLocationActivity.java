package com.worldventures.dreamtrips.modules.picklocation.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationPermissionHelper;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationSettingsDelegate;

import javax.inject.Inject;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class PickLocationActivity extends BaseActivity {

    @Inject
    LocationSettingsDelegate locationSettingsDelegate;

    PickLocationViewImpl view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new PickLocationViewImpl(this);
        view.setId(R.id.master_container);
        view.setLocationPermissionHelper(new LocationPermissionHelper(this));
        setContentView(view);
    }

    public void askForLocationPermission() {
        PickLocationActivityPermissionsDispatcher.locationPermissionWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationPermission() {
        view.getPresenter().onLocationPermissionGranted();
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showRationaleForLocation(PermissionRequest request) {
        view.getPresenter().onRationalForLocationPermissionRequired();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showDeniedForLocation() {
        view.getPresenter().onLocationPermissionDenied();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        PickLocationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!locationSettingsDelegate.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
