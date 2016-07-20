package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
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
    DtlLocationInteractor locationInteractor;
    @Inject
    DtlFilterMerchantInteractor filterInteractor;

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

    public void proceedNavigation(@Nullable Location newLocation) {
        locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
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
                            locationInteractor.locationPipe().send(DtlLocationCommand.change(dtlLocation));
                            navigatePath(DtlMerchantsPath.withAllowedRedirection());
                        }
                    } else {
                        switch (command.getResult().getLocationSourceType()) {
                            case NEAR_ME:
                                if (newLocation == null) { // we had location before, but not now - and we need it
                                    locationInteractor.locationPipe().send(DtlLocationCommand.change(DtlLocation.UNDEFINED));
                                    filterInteractor.filterMerchantsActionPipe().clearReplays();
                                    navigatePath(DtlLocationsPath.getDefault());
                                    break;
                                }
                                //
                                if (!DtlLocationHelper.checkLocation(0.5, newLocation,
                                        command.getResult().getCoordinates().asAndroidLocation(), DistanceType.MILES))
                                    filterInteractor.filterMerchantsActionPipe().clearReplays();
                                //
                                navigatePath(DtlMerchantsPath.withAllowedRedirection());
                                break;
                            case FROM_MAP:
                                navigatePath(DtlMerchantsPath.getDefault());
                                break;
                            case EXTERNAL:
                                navigatePath(DtlMerchantsPath.getDefault());
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
        locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
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
