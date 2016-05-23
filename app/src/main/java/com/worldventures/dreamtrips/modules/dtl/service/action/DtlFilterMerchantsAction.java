package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.JanetPlainActionComposer;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionPipe;
import io.techery.janet.CommandActionBase;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class DtlFilterMerchantsAction extends CommandActionBase<List<DtlMerchant>> {

    private final ReadActionPipe<DtlMerchantsAction> merchantsActionPipe;
    private final ActionPipe<DtlLocationCommand> locationActionPipe;
    private final LocationDelegate locationDelegate;
    private final DtlFilterData filterData;


    public DtlFilterMerchantsAction(DtlFilterData filterData, ReadActionPipe<DtlMerchantsAction> merchantsActionPipe,
                                    ActionPipe<DtlLocationCommand> locationActionPipe, LocationDelegate locationDelegate) {
        this.merchantsActionPipe = merchantsActionPipe;
        this.locationActionPipe = locationActionPipe;
        this.locationDelegate = locationDelegate;
        this.filterData = filterData;
    }

    @Override
    protected void run(CommandCallback<List<DtlMerchant>> callback) throws Throwable {
        getSearchLocation()
                .flatMap(latLng ->
                        merchantsActionPipe.observeWithReplay()
                                .first()
                                .compose(JanetPlainActionComposer.instance())
                                .map(DtlMerchantsAction::getResult)
                                .map(merchants -> {
                                    Queryable.from(merchants)
                                            .filter(DtlMerchant::hasPerks)
                                            .forEachR(DtlMerchant::sortPerks);
                                    DtlMerchantsPredicate predicate = DtlMerchantsPredicate.fromFilterData(filterData);
                                    merchants = Queryable.from(merchants)
                                            .map(element -> patchMerchantDistance(element, latLng, filterData.getDistanceType()))
                                            .filter(predicate)
                                            .sort(DtlMerchant.DISTANCE_COMPARATOR::compare)
                                            .toList();
                                    return merchants;
                                }))
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<LatLng> getSearchLocation() {
        return locationActionPipe.createObservableSuccess(DtlLocationCommand.last())
                .filter(DtlLocationCommand::isResultDefined)
                .map(DtlLocationCommand::getResult)
                .distinct(DtlLocation::getCoordinates)
                .flatMap(location -> locationDelegate.getLastKnownLocationOrEmpty()
                        .onErrorResumeNext(Observable.empty())
                        .switchIfEmpty(locationDelegate.requestLocationUpdate()
                                .take(1)
                                .timeout(1, TimeUnit.SECONDS)
                                .onErrorReturn(throwable -> location.getCoordinates().asAndroidLocation()))
                        .map(last -> DtlLocationHelper.selectAcceptableLocation(last,
                                location)));
    }

    private static DtlMerchant patchMerchantDistance(DtlMerchant merchant, LatLng currentLatLng,
                                                     DistanceType distanceType) {
        merchant.setDistanceType(distanceType);
        merchant.setDistance(DtlLocationHelper.calculateDistance(
                currentLatLng, merchant.getCoordinates().asLatLng()));
        return merchant;
    }
}
