package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantsBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import javax.inject.Inject;

import icepick.State;
import timber.log.Timber;

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
        if (dtlLocation != null) {
            TrackingHelper.dtlLocationLoaded(dtlLocation.getId());
            view.openMerchants(new MerchantsBundle(dtlLocation));
        }
        else
            view.openDtlLocationsScreen();
    }

    public void onEvent(RequestLocationUpdateEvent event) {
        view.checkPermissions();
    }

    public void permissionGranted() {
        view.bind(locationDelegate.requestLocationUpdate()
                .compose(new IoToMainComposer<>()))
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    private void onStatusError(Status status) {
        view.resolutionRequired(status);
    }

    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            onStatusError(((LocationDelegate.LocationException) e).getStatus());
        else Timber.e(e, "Something went wrong while location update");
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

        void openMerchants(MerchantsBundle merchantsBundle);
    }
}
