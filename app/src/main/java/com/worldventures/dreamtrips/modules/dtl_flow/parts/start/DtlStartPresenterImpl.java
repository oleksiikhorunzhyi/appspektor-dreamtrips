package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantStore;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
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
    DtlFilterMerchantStore filterMerchantStore;

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
        navigatePath(DtlLocationsPath.getDefault());
//        gpsLocationDelegate.requestLocationUpdate()
//                .compose(bindViewIoToMainComposer())
//                .take(1)
//                .doOnSubscribe(getView()::showProgress)
//                .subscribe(this::proceedNavigation, this::onLocationError);
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
                        switch (command.getResult().getLocationSourceType()) {
                            case NEAR_ME:
                                if (newLocation == null) { // we had location before, but not now - and we need it
                                    dtlLocationManager.cleanLocation();
                                    filterMerchantStore.filteredMerchantsChangesPipe().clearReplays();
                                    navigatePath(DtlLocationsPath.getDefault());
                                    break;
                                }
                                //
                                if (!DtlLocationHelper.checkLocation(0.5, newLocation,
                                        command.getResult().getCoordinates().asAndroidLocation(), DistanceType.MILES))
                                    filterMerchantStore.filteredMerchantsChangesPipe().clearReplays();
                                //
                                navigatePath(new DtlMerchantsPath());
                                break;
                            case FROM_MAP:
                                navigatePath(new DtlMerchantsPath());
                                break;
                            case EXTERNAL:
                                TrackingHelper.dtlLocationLoaded(
                                        ((DtlExternalLocation) command.getResult()).getId());
                                navigatePath(new DtlMerchantsPath());
                                break;
                        }
                    }
                });
    }

    private void navigatePath(Path path) {
        History history = History.single(path);
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
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
                            if (location.getLocationSourceType() != LocationSourceType.NEAR_ME) {
                                proceedNavigation(null);
                            } else {
                                if (e instanceof LocationDelegate.LocationException)
                                    getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
                                else onLocationResolutionDenied();
                            }
                        },
                        throwable -> navigatePath(DtlLocationsPath.getDefault()));
    }
}
