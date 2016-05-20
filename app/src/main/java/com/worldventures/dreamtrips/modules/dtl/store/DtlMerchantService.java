package com.worldventures.dreamtrips.modules.dtl.store;

import android.annotation.SuppressLint;
import android.location.Location;

import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateAmenitiesAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.WriteActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DtlMerchantService {

    private final DtlLocationService locationService;

    private final ActionPipe<DtlUpdateAmenitiesAction> updateAmenitiesPipe;
    private final ActionPipe<DtlMerchantsAction> merchantsPipe;

    private final WriteActionPipe<DtlFilterDataAction> filterDataActionPipe;

    public DtlMerchantService(Janet janet,
                              DtlLocationService locationService) {
        this.locationService = locationService;

        updateAmenitiesPipe = janet.createPipe(DtlUpdateAmenitiesAction.class, Schedulers.io());
        merchantsPipe = janet.createPipe(DtlMerchantsAction.class, Schedulers.io());

        filterDataActionPipe = janet.createPipe(DtlFilterDataAction.class, Schedulers.io());

        connectMerchantsPipe();
        connectUpdateAmenitiesPipe();
    }

    private void connectMerchantsPipe() {
        merchantsPipe.observeSuccess()
                .filter(DtlMerchantsAction::isFromApi)
                .subscribe(action -> {
                    tryUpdateLocation(action.getCacheData());
                    updateAmenitiesPipe.send(new DtlUpdateAmenitiesAction(action.getCacheData()));
                }, Throwable::printStackTrace);
        merchantsPipe.send(DtlMerchantsAction.fromCache());
    }

    private void connectUpdateAmenitiesPipe() {
        updateAmenitiesPipe.observeSuccess()
                .subscribe(action ->
                        filterDataActionPipe.send(DtlFilterDataAction.amenitiesUpdate(action.getResult()))
                );
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("unchecked")
    public void loadMerchants(Location location) {
        String locationArg = String.format("%1$f,%2$f",
                location.getLatitude(), location.getLongitude());
        merchantsPipe.send(DtlMerchantsAction.fromApi(locationArg));
    }

    public ReadActionPipe<DtlMerchantsAction> merchantsActionPipe() {
        return merchantsPipe;
    }

    public Observable<DtlMerchant> getMerchantById(String merchantId) {
        return merchantsActionPipe().observeSuccessWithReplay()
                .first()
                .map(DtlMerchantsAction::getCacheData)
                .flatMap(Observable::from)
                .filter(merchant -> merchant.getId().equals(merchantId));
    }

    private void tryUpdateLocation(List<DtlMerchant> dtlMerchants) {
        locationService.locationPipe().createObservableSuccess(DtlLocationCommand.get())
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
                    locationService.locationPipe().send(DtlLocationCommand.change(updatedLocation));
                }, Throwable::printStackTrace);
    }
}
