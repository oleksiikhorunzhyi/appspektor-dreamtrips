package com.worldventures.dreamtrips.modules.dtl.action;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantStore;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

@CommandAction
public class DtlFilterMerchantsAction extends CommandActionBase<List<DtlMerchant>> {

    private final DtlMerchantStore merchantStore;
    private final DtlLocationManager dtlLocationManager;
    private final LocationDelegate locationDelegate;
    private final DtlFilterData filterData;


    public DtlFilterMerchantsAction(DtlFilterData filterData, DtlMerchantStore merchantStore,
                                    DtlLocationManager dtlLocationManager, LocationDelegate locationDelegate) {
        this.merchantStore = merchantStore;
        this.dtlLocationManager = dtlLocationManager;
        this.locationDelegate = locationDelegate;
        this.filterData = filterData;
    }

    @Override
    protected void run(CommandCallback<List<DtlMerchant>> callback) throws Throwable {
        getSearchLocation()
                .flatMap(latLng ->
                        merchantStore.getState()
                                .compose(new ActionStateToActionTransformer<>())
                                .map(CommandActionBase::getResult)
                                .map(merchants -> {
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
        return dtlLocationManager.getSelectedLocation()
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
