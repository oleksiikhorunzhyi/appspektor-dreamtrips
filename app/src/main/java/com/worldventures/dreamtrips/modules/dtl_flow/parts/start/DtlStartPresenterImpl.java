package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import javax.inject.Inject;

import flow.Flow;

public class DtlStartPresenterImpl extends FlowPresenterImpl<DtlStartScreen, ViewState.EMPTY>
        implements DtlStartPresenter {

    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlMerchantManager dtlMerchantManager;

    public DtlStartPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        bindLocationObtaining();
    }

    private void bindLocationObtaining() {
        gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .take(1)
                .doOnSubscribe(getView()::showProgress)
                .subscribe(this::proceedNavigation, this::onLocationError);
    }

    public void onLocationResolutionGranted() {
        bindLocationObtaining();
    }

    public void onLocationResolutionDenied() {
        proceedNavigation(null);
    }

    /**
     * Determines whether we can proceed without locating device by GPS.
     */
    private boolean needsLocation() {
        DtlLocation persistedLocation = dtlLocationManager.getSelectedLocation();
        return persistedLocation != null && persistedLocation.getLocationSourceType() == LocationSourceType.NEAR_ME;
    }

    public void proceedNavigation(@Nullable Location newLocation) {
        DtlLocation persistedLocation = dtlLocationManager.getSelectedLocation();
        if (persistedLocation == null) {
            if (newLocation == null) Flow.get(getContext()).set(new DtlLocationsPath());
            else {
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(newLocation))
                        .build();
                dtlLocationManager.persistLocation(dtlLocation);
                Flow.get(getContext()).set(new DtlMerchantsPath());
            }
        } else {
            switch (persistedLocation.getLocationSourceType()) {
                case NEAR_ME:
                    if (newLocation == null) { // we had location before, but not now - and we need it
                        dtlLocationManager.cleanLocation();
                        dtlMerchantManager.clean();
                        Flow.get(getContext()).set(new DtlLocationsPath());
                        break;
                    }
                    //
                    if (!DtlLocationHelper.checkLocation(0.5, newLocation,
                            persistedLocation.getCoordinates().asAndroidLocation(), DistanceType.MILES))
                        dtlMerchantManager.clean();
                    //
                    Flow.get(getContext()).set(new DtlMerchantsPath());
                    break;
                case FROM_MAP:
                    Flow.get(getContext()).set(new DtlMerchantsPath());
                    break;
                case EXTERNAL:
                    TrackingHelper.dtlLocationLoaded(
                            ((DtlExternalLocation) dtlLocationManager.getSelectedLocation()).getId());
                    Flow.get(getContext()).set(new DtlMerchantsPath());
                    break;
            }
        }
    }

    /**
     * Check if given error's cause is insufficient GPS resolution or usual throwable and act accordingly
     * @param e exception that {@link LocationDelegate} subscription returned
     */
    private void onLocationError(Throwable e) {
        if (!needsLocation()) {
            proceedNavigation(null);
            return;
        } else {
            if (e instanceof LocationDelegate.LocationException)
//                getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
                getView().informUser("TURN ON YOUR GPS!!!"); // TODO :: 3/31/16 replace after activity delegate for this implemented
            else onLocationResolutionDenied();
        }
    }
}
