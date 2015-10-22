package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.techery.spares.annotations.State;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.DtlModule;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;

import javax.inject.Inject;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View> {

    @Inject
    SnappyRepository db;

    @State
    DtlLocationsHolder dtlLocationsHolder;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.startLoading();
    }

    private Subscription locationSubscription;

    public void permissionGranted() {
        if (dtlLocationsHolder != null) {
            onLocationLoaded(dtlLocationsHolder);
            return;
        }
        //
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        locationSubscription = locationProvider.checkLocationSettings(
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(request)
                        .setAlwaysShow(true)
                        .build()
        ).doOnNext(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                view.resolutionRequired(status);
            }
        }).flatMap(locationSettingsResult -> locationProvider.getUpdatedLocation(request)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLocationObtained, this::onLocationError);

    }

    private void unsubscribeFromLocationUpdate() {
        if (locationSubscription != null && !locationSubscription.isUnsubscribed())
            locationSubscription.unsubscribe();
    }

    @Override
    public void dropView() {
        super.dropView();
        unsubscribeFromLocationUpdate();
    }

    public void locationNotGranted() {
        loadCities(DtlModule.LAT, DtlModule.LNG);
    }

    private void onLocationError(Throwable e) {
        Timber.e(e, "Location update error");
        loadCities(DtlModule.LAT, DtlModule.LNG);
    }

    private void onLocationObtained(Location location) {
        loadCities(location.getLatitude(), location.getLongitude());
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

        void resolutionRequired(Status status);

        void openLocation(PlacesBundle bundle);
    }
}
