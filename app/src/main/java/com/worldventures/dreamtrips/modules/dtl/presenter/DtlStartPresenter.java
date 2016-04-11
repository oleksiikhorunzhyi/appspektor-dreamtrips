package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

import icepick.State;

public class DtlStartPresenter extends JobPresenter<DtlStartPresenter.View> {

    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    //
    @State
    boolean initialized;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        //
        if (initialized) return;
        initialized = true;
        //
        bindLocationObtaining();
    }

    private void bindLocationObtaining() {
        view.bind(gpsLocationDelegate.requestLocationUpdate()
                .take(1)
                .compose(new IoToMainComposer<>()))
                .doOnSubscribe(view::showProgress)
                .subscribe(this::proceedNavigation, this::onLocationError);
    }

    public void onLocationResolutionGranted() {
        bindLocationObtaining();
    }

    public void onLocationResolutionDenied() {
        proceedNavigation(null);
    }

    public void proceedNavigation(@Nullable Location newLocation) {
        dtlLocationManager.getSelectedLocation()
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> {
                    if (!command.isResultDefined()) {
                        if (newLocation == null) view.openDtlLocationsScreen();
                        else {
                            DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                                    .locationSourceType(LocationSourceType.NEAR_ME)
                                    .longName(context.getString(R.string.dtl_near_me_caption))
                                    .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(newLocation))
                                    .build();
                            dtlLocationManager.persistLocation(dtlLocation);
                            view.openMerchants();
                        }
                    } else {
                        switch (command.getResult().getLocationSourceType()) {
                            case NEAR_ME:
                                if (newLocation == null) { // we had location before, but not now - and we need it
                                    dtlLocationManager.cleanLocation();
                                    dtlMerchantManager.clean();
                                    view.openDtlLocationsScreen();
                                    break;
                                }
                                //
                                if (!DtlLocationHelper.checkLocation(0.5, newLocation,
                                        command.getResult().getCoordinates().asAndroidLocation(), DistanceType.MILES))
                                    dtlMerchantManager.clean();
                                //
                                view.openMerchants();
                                break;
                            case FROM_MAP:
                                view.openMerchants();
                                break;
                            case EXTERNAL:
                                TrackingHelper.dtlLocationLoaded(
                                        ((DtlExternalLocation) command.getResult()).getId());
                                view.openMerchants();
                                break;
                        }
                    }
                });
    }

    /**
     * Check if given error's cause is insufficient GPS resolution or usual throwable and act accordingly
     *
     * @param e exception that {@link LocationDelegate} subscription returned
     */
    private void onLocationError(Throwable e) {
        dtlLocationManager.getSelectedLocation()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> {
                    // Determines whether we can proceed without locating device by GPS.
                    if (location.getLocationSourceType() == LocationSourceType.NEAR_ME) {
                        proceedNavigation(null);
                    } else {
                        if (e instanceof LocationDelegate.LocationException)
                            view.locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
                        else onLocationResolutionDenied();
                    }
                });
    }

    public interface View extends RxView {

        void locationResolutionRequired(Status status);

        void showProgress();

        void hideProgress();

        void openDtlLocationsScreen();

        void openMerchants();
    }
}
