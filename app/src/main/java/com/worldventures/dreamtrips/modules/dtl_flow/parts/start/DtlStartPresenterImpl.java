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
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;

public class DtlStartPresenterImpl extends DtlPresenterImpl<DtlStartScreen, ViewState.EMPTY>
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
            if (newLocation == null) navigatePath(DtlLocationsPath.getDefault());
            else {
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(newLocation))
                        .build();
                dtlLocationManager.persistLocation(dtlLocation);
                navigatePath(new DtlMerchantsPath());
            }
        } else {
            switch (persistedLocation.getLocationSourceType()) {
                case NEAR_ME:
                    if (newLocation == null) { // we had location before, but not now - and we need it
                        dtlLocationManager.cleanLocation();
                        dtlMerchantManager.clean();
                        navigatePath(DtlLocationsPath.getDefault());
                        break;
                    }
                    //
                    if (!DtlLocationHelper.checkLocation(0.5, newLocation,
                            persistedLocation.getCoordinates().asAndroidLocation(), DistanceType.MILES))
                        dtlMerchantManager.clean();
                    //
                    navigatePath(new DtlMerchantsPath());
                    break;
                case FROM_MAP:
                    navigatePath(new DtlMerchantsPath());
                    break;
                case EXTERNAL:
                    TrackingHelper.dtlLocationLoaded(
                            ((DtlExternalLocation) dtlLocationManager.getSelectedLocation()).getId());
                    navigatePath(new DtlMerchantsPath());
                    break;
            }
        }
    }

    private void navigatePath(Path path) {
        History history = History.single(path);
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
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
                getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
            else onLocationResolutionDenied();
        }
    }
}
