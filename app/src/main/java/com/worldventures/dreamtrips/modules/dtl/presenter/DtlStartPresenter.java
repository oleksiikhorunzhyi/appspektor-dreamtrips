package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.PermissionView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationRepository;

import javax.inject.Inject;

import icepick.State;
import timber.log.Timber;

public class DtlStartPresenter extends Presenter<DtlStartPresenter.View> {

    @State
    boolean initialized;

    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    DtlLocationRepository dtlLocationRepository;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        gpsLocationDelegate.setPermissionView(view);
        //
        if (initialized) return;
        initialized = true;
        //
        DtlLocation dtlLocation = dtlLocationRepository.getSelectedLocation();
        if (dtlLocation != null) {
            TrackingHelper.dtlLocationLoaded(dtlLocation.getId());
            view.openMerchants(new PlacesBundle(dtlLocation));
        } else {
            view.openDtlLocationsScreen();
        }
    }

    public void permissionGranted() {
        view.bind(gpsLocationDelegate.requestLocationUpdate()
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
        gpsLocationDelegate.onLocationObtained(location);
    }

    public void locationNotGranted() {
        gpsLocationDelegate.onLocationObtained(null);
    }

    public interface View extends RxView, PermissionView {
        void checkPermissions();

        void resolutionRequired(Status status);

        void openDtlLocationsScreen();

        void openMerchants(PlacesBundle placesBundle);
    }
}
