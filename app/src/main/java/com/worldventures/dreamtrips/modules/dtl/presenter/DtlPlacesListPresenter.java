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
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlMerchantDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchPlaceRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class DtlPlacesListPresenter extends Presenter<DtlPlacesListPresenter.View> implements
        DtlFilterDelegate.FilterListener, DtlMerchantDelegate.MerchantUpdatedListener {

    @Inject
    SnappyRepository db;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantDelegate dtlMerchantDelegate;

    protected DtlMerchantType placeType;

    private DtlLocation dtlLocation;

    public DtlPlacesListPresenter(DtlMerchantType placeType) {
        this.placeType = placeType;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlFilterDelegate.addListener(this);
        dtlMerchantDelegate.attachListener(this);
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
        dtlMerchantDelegate.detachListener(this);
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
                        .flatMap(location -> filter(location, query))
                        .compose(new IoToMainComposer<>())
        ).subscribe(view::setItems, this::onError);
    }

    private Observable<List<DtlMerchant>> filter(Location location, String query) {
        LatLng currentLatLng = LocationHelper.getAcceptedLocation(location, dtlLocation);
        //
        List<DtlMerchant> places = Queryable.from(dtlMerchantDelegate.getMerchants())
                .filter(dtlMerchant -> placeType == null ||
                        dtlMerchant.getMerchantType() == placeType)
                .filter(dtlMerchant -> dtlMerchant.applyFilter(dtlFilterDelegate.getDtlFilterData(),
                        currentLatLng))
                .filter(dtlMerchant -> dtlMerchant.containsQuery(query)).toList();

        for (DtlMerchant DtlMerchant : places) {
            DtlMerchant.calculateDistance(currentLatLng);
        }

        Collections.sort(places, DtlMerchant.DISTANCE_COMPARATOR);

        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, places.size());

        return Observable.from(places).toList();
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