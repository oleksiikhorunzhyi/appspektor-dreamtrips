package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlUpdateAmenitiesAction;

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class DtlMerchantInteractor {

    private final DtlLocationInteractor locationInteractor;

    private final ActionPipe<DtlUpdateAmenitiesAction> updateAmenitiesPipe;
    private final ActionPipe<DtlMerchantsAction> merchantsPipe;
    private final ActionPipe<DtlMerchantByIdAction> merchantByIdPipe;

    private final WriteActionPipe<DtlFilterDataAction> filterDataActionPipe;

    public DtlMerchantInteractor(Janet janet,
                                 DtlLocationInteractor locationInteractor) {
        this.locationInteractor = locationInteractor;

        updateAmenitiesPipe = janet.createPipe(DtlUpdateAmenitiesAction.class, Schedulers.io());
        merchantsPipe = janet.createPipe(DtlMerchantsAction.class, Schedulers.io());
        merchantByIdPipe = janet.createPipe(DtlMerchantByIdAction.class);
        filterDataActionPipe = janet.createPipe(DtlFilterDataAction.class, Schedulers.io());

        connectMerchantsPipe();
        connectUpdateAmenitiesPipe();
    }

    private void connectMerchantsPipe() {
        merchantsPipe.observeSuccess()
                .filter(DtlMerchantsAction::isFromApi)
                .subscribe(action -> {
                    tryUpdateLocation(action.getResult());
                    updateAmenitiesPipe.send(new DtlUpdateAmenitiesAction(action.getResult()));
                }, Throwable::printStackTrace);
        merchantsPipe.send(DtlMerchantsAction.restore());
    }

    private void connectUpdateAmenitiesPipe() {
        updateAmenitiesPipe.observeSuccess()
                .subscribe(action ->
                        filterDataActionPipe.send(DtlFilterDataAction.amenitiesUpdate(action.getResult()))
                );
    }

    public ActionPipe<DtlMerchantsAction> merchantsActionPipe() {
        return merchantsPipe;
    }

    public ActionPipe<DtlMerchantByIdAction> merchantByIdPipe() {
        return merchantByIdPipe;
    }

    //TODO: move to action
    private void tryUpdateLocation(List<DtlMerchant> dtlMerchants) {
        locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
                .filter(command -> {
                    LocationSourceType sourceType = command.getResult().getLocationSourceType();
                    return (sourceType == LocationSourceType.FROM_MAP || sourceType == LocationSourceType.NEAR_ME)
                            && !dtlMerchants.isEmpty();
                })
                .map(DtlLocationCommand::getResult)
                .subscribe(location -> {
                    DtlMerchant nearestMerchant = dtlMerchants.get(0);
                    DtlLocation updatedLocation = ImmutableDtlManualLocation
                            .copyOf((DtlManualLocation) location)
                            .withLongName(location.getLocationSourceType() == LocationSourceType.FROM_MAP
                                    ? nearestMerchant.getCity() : location.getLongName())
                            .withAnalyticsName(nearestMerchant.getAnalyticsName());
                    locationInteractor.locationPipe().send(DtlLocationCommand.change(updatedLocation));
                }, Throwable::printStackTrace);
    }
}
