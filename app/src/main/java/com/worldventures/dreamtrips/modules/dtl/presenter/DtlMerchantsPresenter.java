package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import techery.io.library.JobSubscriber;
import timber.log.Timber;

public abstract class DtlMerchantsPresenter<VT extends RxView> extends JobPresenter<VT> {

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    protected DtlMerchantType dtlMerchantType;

    @Override
    public void takeView(VT view) {
        super.takeView(view);
        bindApiJob();
        bindFiltering();
    }

    protected void bindFiltering() {
        view.bind(dtlFilterDelegate.getFilterStream()).subscribe(this::onFilter);
    }

    protected JobSubscriber bindApiJob() {
        return bindJobCached(dtlMerchantManager.getMerchantsExecutor)
                .onSuccess(dtlMerchants -> onMerchantsLoaded());
    }

    protected void onMerchantsLoaded() {
        performFiltering();
    }

    public void onFilter(DtlFilterData filterData) {
        performFiltering();
    }

    protected void performFiltering() {
        performFiltering(dtlFilterDelegate.getFilterData());
    }

    protected void performFiltering(DtlFilterData filterData) {
        if (view == null) return;
        //
        bindLocationDelegate()
                .flatMap(location -> {
                    return Observable.just(DtlLocationHelper.selectAcceptableLocation(location,
                            dtlLocationManager.getCachedSelectedLocation()));
                })
                .flatMap(coordinates -> filterMechantsList(coordinates, filterData))
                .doOnNext(merchants -> trackMerchantSearch(merchants, filterData.getSearchQuery()))
                .compose(new IoToMainComposer<>())
                .subscribe(this::merchantsPrepared, this::onError);
    }

    private Observable<Location> bindLocationDelegate() {
        return view.bind(locationDelegate.getLastKnownLocation()
                .onErrorResumeNext(Observable.just(dtlLocationManager.getCachedSelectedLocation()
                        .asAndroidLocation())));
    }

    protected Observable<List<DtlMerchant>> filterMechantsList(LatLng acceptedLocation, DtlFilterData filterData) {
        List<DtlMerchant> dtlMerchants = dtlMerchantManager.getMerchants();
        //
        Queryable.from(dtlMerchants).forEachR(merchant ->
                patchMerchantDistance(merchant, acceptedLocation, filterData.getDistanceType()));
        //
        List<DtlMerchant> merchants = Queryable.from(dtlMerchants)
                .filter(ImmutableDtlMerchantsPredicate.builder()
                        .merchantType(dtlMerchantType)
                        .filterData(filterData)
                        .currentLatLng(acceptedLocation)
                        .build()).toList();
        //
        afterMapping(merchants);
        //
        return Observable.from(merchants).toList();
    }

    protected void patchMerchantDistance(DtlMerchant merchant, LatLng currentLatLng,
                                         DistanceType distanceType) {
        merchant.setDistanceType(distanceType);
        merchant.setDistance(DtlLocationHelper.calculateDistance(
                currentLatLng, merchant.getCoordinates().asLatLng()));
    }

    protected void afterMapping(List<DtlMerchant> merchants) {
        // so that ancestors could do something more with mapped collection
    }

    private void trackMerchantSearch(List<DtlMerchant> merchants, String query) {
        if (!TextUtils.isEmpty(query)) TrackingHelper.dtlMerchantSearch(query, merchants.size());
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
    }

    protected abstract void merchantsPrepared(List<DtlMerchant> dtlMerchants);
}
