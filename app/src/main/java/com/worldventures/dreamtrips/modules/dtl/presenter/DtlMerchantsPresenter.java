package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlSearchDelegate;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public abstract class DtlMerchantsPresenter<VT extends RxView> extends Presenter<VT> implements
        DtlFilterDelegate.FilterListener, DtlMerchantRepository.MerchantUpdatedListener,
        DtlSearchDelegate.SearchListener {

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;
    @Inject
    DtlLocationManager locationRepository;
    @Inject
    DtlSearchDelegate dtlSearchDelegate;
    @Inject
    SnappyRepository snappyRepository;
    //
    protected DtlMerchantType dtlMerchantType;

    @Override
    public void takeView(VT view) {
        super.takeView(view);
        dtlMerchantRepository.attachListener(this);
        dtlFilterDelegate.addListener(this);
        dtlSearchDelegate.addListener(this);
    }

    @Override
    public void dropView() {
        dtlMerchantRepository.detachListener(this);
        dtlFilterDelegate.removeListener(this);
        dtlSearchDelegate.removeListener(this);
        super.dropView();
    }

    @Override
    public void onMerchantsUploaded() {
        performFiltering();
    }

    @Override
    public void onMerchantsFailed(SpiceException exception) {
        //
    }

    @Override
    public void onFilter() {
        performFiltering();
    }

    @Override
    public void onSearch(String query) {
        performFiltering(query);
    }

    protected void performFiltering() {
        performFiltering("");
    }

    protected void performFiltering(String query) {
        if (view != null)
            view.bind(locationDelegate
                    .getLastKnownLocation()
                    .onErrorResumeNext(Observable.just(locationRepository.getCachedSelectedLocation()
                            .asAndroidLocation()))
                    .flatMap(location -> mapToMerchantList(location, query))
                    .doOnNext(merchants -> track(merchants, query))
                    .compose(new IoToMainComposer<>())
            ).subscribe(this::merchantsPrepared, this::onError);
    }

    protected Observable<List<DtlMerchant>> mapToMerchantList(Location location, String query) {
        List<DtlMerchant> dtlMerchants = dtlMerchantRepository.getMerchants();
        //
        DtlLocationHelper dtlLocationHelper = new DtlLocationHelper();
        LatLng currentLatLng = dtlLocationHelper.getAcceptedLocation(location, locationRepository.getCachedSelectedLocation());
        DtlFilterData dtlFilterData = dtlFilterDelegate.getDtlFilterData();
        DtlFilterData.DistanceType distanceType = snappyRepository.getMerchantsDistanceType();
        //
        for (DtlMerchant dtlMerchant : dtlMerchants) {
            dtlMerchant.setDistanceType(distanceType);
            dtlMerchant.setDistance(dtlLocationHelper.calculateDistance(currentLatLng, dtlMerchant.getCoordinates().asLatLng()));
        }
        //
        List<DtlMerchant> merchants = Queryable
                .from(dtlMerchants)
                .filter(DtlMerchantsPredicate.Builder.create()
                        .withMerchantType(dtlMerchantType)
                        .withDtlFilterData(dtlFilterData)
                        .withLatLng(currentLatLng)
                        .withQuery(query)
                        .build())
                .toList();
        //
        afterMapping(merchants);
        //
        return Observable.from(merchants).toList();
    }

    protected void afterMapping(List<DtlMerchant> merchants) {
        // so that ancestors could do something more with mapped collection
    }

    private void track(List<DtlMerchant> merchants, String query) {
        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, merchants.size());
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
    }

    protected abstract void merchantsPrepared(List<DtlMerchant> dtlMerchants);

}
