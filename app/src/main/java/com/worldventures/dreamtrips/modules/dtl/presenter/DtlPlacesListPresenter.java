package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantStore;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchPlaceRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class DtlPlacesListPresenter extends Presenter<DtlPlacesListPresenter.View> implements
        DtlFilterDelegate.FilterListener, DtlMerchantStore.MerchantUpdatedListener {

    @Inject
    SnappyRepository db;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantStore dtlMerchantStore;

    protected DtlMerchantType placeType;

    private DtlLocation dtlLocation;

    public DtlPlacesListPresenter(DtlMerchantType placeType) {
        this.placeType = placeType;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlFilterDelegate.addListener(this);
        dtlMerchantStore.attachListener(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlLocation = db.getSelectedDtlLocation();
        //
        view.showProgress();
        //
        performFiltering();
        //
        if (placeType == DtlMerchantType.OFFER) view.setComingSoon();
    }

    @Override
    public void dropView() {
        dtlFilterDelegate.removeListener(this);
        dtlMerchantStore.detachListener(this);
        super.dropView();
    }

    @Override
    public void onMerchantsUploaded() {
        performFiltering();
    }

    @Override
    public void onMerchantsFailed(SpiceException spiceException) {
        view.hideProgress();
    }

    @Override
    public void onFilter() {
        performFiltering();
    }

    public void onEventMainThread(DtlSearchPlaceRequestEvent event) {
        performFiltering(event.getSearchQuery());
    }

    private void performFiltering() {
        performFiltering("");
    }

    private void performFiltering(String query) {
        view.bind(locationDelegate
                        .getLastKnownLocation()
                        .onErrorResumeNext(Observable.just(dtlLocation.asAndroidLocation()))
                        .flatMap(location -> mapToMerchantList(location, query))
                        .doOnNext(merchants -> track(merchants, query))
                        .compose(new IoToMainComposer<>())
        ).subscribe(view::setItems, this::onError);
    }

    private Observable<List<DtlMerchant>> mapToMerchantList(Location location, String query) {
        LatLng currentLatLng = LocationHelper.getAcceptedLocation(location, dtlLocation);
        //
        List<DtlMerchant> merchants = Queryable
                .from(dtlMerchantStore.getMerchants())
                .filter(DtlMerchantsPredicate.Builder.create()
                        .withDtlFilterData(dtlFilterDelegate.getDtlFilterData())
                        .withLatLng(currentLatLng)
                        .withMerchantType(placeType)
                        .withQuery(query)
                        .build())
                .toList();
        //
        sort(merchants, currentLatLng);
        //
        return Observable.from(merchants).toList();
    }

    private void sort(List<DtlMerchant> merchants, LatLng latLng) {
        for (DtlMerchant dtlMerchant : merchants) {
            dtlMerchant.calculateDistance(latLng);
        }

        Collections.sort(merchants, DtlMerchant.DISTANCE_COMPARATOR);
    }

    private void track(List<DtlMerchant> merchants, String query) {
        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, merchants.size());
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
    }

    public void onEventMainThread(TogglePlaceSelectionEvent event) {
        view.toggleSelection(event.getDtlMerchant());
    }

    public interface View extends RxView {

        void setItems(List<DtlMerchant> places);

        void showProgress();

        void hideProgress();

        void toggleSelection(DtlMerchant DtlMerchant);

        void setComingSoon();
    }
}
