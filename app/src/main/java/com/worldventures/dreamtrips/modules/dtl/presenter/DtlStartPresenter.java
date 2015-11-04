package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import javax.inject.Inject;

import icepick.State;
import rx.Subscription;

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

    private Subscription locationSubscription;

    public void permissionGranted() {
        locationSubscription = locationDelegate.requestLocationUpdates(view::resolutionRequired,
                this::onLocationObtained,
                this::onLocationError);
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

        void openMerchants(PlacesBundle placesBundle);
    }
}
