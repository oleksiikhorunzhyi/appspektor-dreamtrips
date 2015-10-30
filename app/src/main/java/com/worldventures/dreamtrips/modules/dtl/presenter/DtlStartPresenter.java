package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import javax.inject.Inject;

import icepick.State;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DtlStartPresenter extends Presenter<DtlStartPresenter.View> {

    @Inject
    SnappyRepository db;
    @State
    boolean initialized;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (initialized) return;
        initialized = true;
        //
        DtlLocation location = db.getSelectedDtlLocation();
        if (location == null) {
            view.openDtlLocationsScreen();
        } else {
            view.openDtlPlacesScreen(new PlacesBundle(location));
        }
    }

    public void onEvent(RequestLocationUpdateEvent event) {
        view.checkPermissions();
    }

    private Subscription locationSubscription;

    public void permissionGranted() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        locationSubscription = locationProvider.checkLocationSettings(
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(request)
                        .setAlwaysShow(true)
                        .build()
        ).doOnNext(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                view.resolutionRequired(status);
            }
        }).flatMap(locationSettingsResult -> locationProvider.getUpdatedLocation(request)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLocationObtained, this::onLocationError);

    }

    private void onLocationError(Throwable e) {
        eventBus.post(new LocationObtainedEvent());
    }

    private void onLocationObtained(Location location) {
        eventBus.post(new LocationObtainedEvent(location));
    }

    public void locationNotGranted() {
        eventBus.post(new LocationObtainedEvent());
    }

    @Override
    public void dropView() {
        super.dropView();
        if (locationSubscription != null && !locationSubscription.isUnsubscribed())
            locationSubscription.unsubscribe();
    }

    public interface View extends Presenter.View {
        void checkPermissions();

        void resolutionRequired(Status status);

        void openDtlLocationsScreen();

        void openDtlPlacesScreen(PlacesBundle bundle);
    }
}
