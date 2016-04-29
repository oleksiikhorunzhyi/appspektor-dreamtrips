package com.worldventures.dreamtrips.modules.picklocation.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationPermissionHelper;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationSettingsDelegate;

import javax.inject.Inject;

import rx.Subscription;

public class PickLocationActivity extends BaseActivity {

    @Inject
    LocationSettingsDelegate locationSettingsDelegate;

    private PickLocationViewImpl view;
    private Subscription permissionSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new PickLocationViewImpl(this);
        view.setId(R.id.master_container);
        view.setLocationPermissionHelper(new LocationPermissionHelper(this));
        setContentView(view);
    }

    public void askForLocationPermission() {
        permissionSubscription = permissionDispatcher
                .requestPermission(PermissionConstants.LOCATION_PERMISSIONS)
                .subscribe(new PermissionSubscriber()
                        .onPermissionRationaleAction(this::showRationaleForLocation)
                        .onPermissionDeniedAction(this::showDeniedForLocation)
                        .onPermissionGrantedAction(this::locationPermissionGranted));
    }

    void locationPermissionGranted() {
        view.getPresenter().onLocationPermissionGranted();
    }

    void showRationaleForLocation() {
        view.getPresenter().onRationalForLocationPermissionRequired();
    }

    void showDeniedForLocation() {
        view.getPresenter().onLocationPermissionDenied();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!locationSettingsDelegate.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (permissionSubscription != null && !permissionSubscription.isUnsubscribed()) {
            permissionSubscription.unsubscribe();
        }
    }
}
