package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import javax.inject.Inject;

import icepick.State;

public class DtlStartPresenter extends Presenter<DtlStartPresenter.View> {

    @State
    boolean initialized;

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    SnappyRepository db;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (initialized) return;
        initialized = true;
        //
        DtlLocation dtlLocation = db.getSelectedDtlLocation();
        if (dtlLocation != null)
            view.openMerchants(new PlacesBundle(dtlLocation));
        else
            view.openDtlLocationsScreen();
    }

    public void onEvent(RequestLocationUpdateEvent event) {
        view.checkPermissions();
    }

    public void permissionGranted() {
        locationDelegate.checkSettings()
                .doOnNext(this::onSettingsObtained)
                .flatMap(locationSettingsResult -> locationDelegate.requestLocationUpdate())
                .compose(new IoToMainComposer<>())
                .compose(RxLifecycle.bindFragment(view.lifecycle()))
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    private void onSettingsObtained(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
            view.resolutionRequired(status);
    }

    private void onLocationError(Throwable e) {
        locationNotGranted();
    }

    private void onLocationObtained(Location location) {
        eventBus.post(new LocationObtainedEvent(location));
    }

    public void locationNotGranted() {
        eventBus.post(new LocationObtainedEvent());
    }

    public interface View extends RxView {
        void checkPermissions();

        void resolutionRequired(Status status);

        void openDtlLocationsScreen();

        void openMerchants(PlacesBundle placesBundle);
    }
}
