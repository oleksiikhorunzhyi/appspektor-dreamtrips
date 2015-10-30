package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.techery.spares.annotations.State;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.DtlModule;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View> {

    @Inject
    SnappyRepository db;

    @State
    DtlLocationsHolder dtlLocationsHolder;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.startLoading();

        if (dtlLocationsHolder != null) {
            onLocationLoaded(dtlLocationsHolder);
            return;
        }

        eventBus.post(new RequestLocationUpdateEvent());
    }

    public void onEvent(LocationObtainedEvent event) {
        if (event.getLocation() == null) loadCities(DtlModule.LAT, DtlModule.LNG);
        else loadCities(event.getLocation().getLatitude(), event.getLocation().getLongitude());
    }

    private void loadCities(double latitude, double longitude) {
        view.citiesLoadingStarted();
        doRequest(new GetDtlLocationsQuery(latitude, longitude),
                this::onLocationLoaded,
                spiceException -> {
                    this.handleError(spiceException);
                    view.finishLoading();
                });
    }

    private void onLocationLoaded(DtlLocationsHolder dtlLocationsHolder) {
        this.dtlLocationsHolder = dtlLocationsHolder;
        view.setItems(dtlLocationsHolder);
        view.finishLoading();
    }

    public void search(String caption) {
        if (view != null)
            Observable.create(new Observable.OnSubscribe<DtlLocationsHolder>() {
                @Override
                public void call(Subscriber<? super DtlLocationsHolder> subscriber) {
                    subscriber.onNext(dtlLocationsHolder.filter(caption));
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setItems);
    }

    public void flushSearch() {
        view.setItems(dtlLocationsHolder);
    }

    public void onLocationClicked(DtlLocation location) {
        db.saveSelectedDtlLocation(location);
        db.clearAllForKey(SnappyRepository.DTL_PLACES_PREFIX);
        view.openLocation(new PlacesBundle(location));
    }

    public interface View extends Presenter.View {

        void setItems(DtlLocationsHolder dtlLocationsHolder);

        void startLoading();

        void finishLoading();

        void citiesLoadingStarted();

        void openLocation(PlacesBundle bundle);
    }
}
