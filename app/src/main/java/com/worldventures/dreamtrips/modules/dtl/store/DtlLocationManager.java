package com.worldventures.dreamtrips.modules.dtl.store;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.transfromer.ListFilter;
import com.worldventures.dreamtrips.core.rx.transfromer.ListSorter;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import techery.io.library.Job0Executor;
import techery.io.library.Job1Executor;

public class DtlLocationManager {

    @Inject
    DtlApi dtlApi;
    @Inject
    SnappyRepository db;
    @Inject
    RxApiFactory rxApiFactory;

    public DtlLocationManager(Injector injector) {
        injector.inject(this);
    }

    public DtlLocationManager(DtlApi dtlApi, SnappyRepository db, RxApiFactory rxApiFactory) {
        this.dtlApi = dtlApi;
        this.db = db;
        this.rxApiFactory = rxApiFactory;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nearby
    ///////////////////////////////////////////////////////////////////////////

    public final Job1Executor<Location, List<DtlExternalLocation>> nearbyLocationExecutor =
            new Job1Executor<>(this::loadNearby);
    private Subscription nearbySubscription;

    public void loadNearbyLocations(Location userLocation) {
        nearbySubscription = nearbyLocationExecutor.createJobWith(userLocation).subscribe();
    }

    private Observable<List<DtlExternalLocation>> loadNearby(Location location) {
        return rxApiFactory.composeApiCall(() ->
                this.dtlApi.getNearbyLocations(location.getLatitude() + ","
                        + location.getLongitude()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    private static final int API_SEARCH_QUERY_LENGHT = 3;

    public final Job0Executor<List<DtlExternalLocation>> searchLocationExecutor =
            new Job0Executor<>(this::search);

    private Subscription searchSubscription;

    private String query;
    private List<DtlExternalLocation> searchLocations;

    public String getQuery() {
        return query;
    }

    public void searchLocations(String query) {
        this.query = query;
        // don't do anything if users enter 'local-search' query and searchLocations is still null
        // if (shouldPerformLocalSearch() && searchLocations == null) return;
        // cancel if user enters new 'non-local-search' query
        if (!shouldPerformLocalSearch()
                && searchSubscription != null
                && !searchSubscription.isUnsubscribed())
            searchSubscription.unsubscribe();
        // cancel nearby call if search started
        if (nearbySubscription != null && !nearbySubscription.isUnsubscribed())
            nearbySubscription.unsubscribe();
        //
        searchSubscription = searchLocationExecutor.createJob().subscribe();
    }

    private Observable<List<DtlExternalLocation>> search() {
        return Observable.concat(emptySearch(), localSearch(), apiSearch())
                .first()
                .compose(prepareLocations());
    }

    private Observable<List<DtlExternalLocation>> emptySearch() {
        return shouldPerformEmptySearch()
                ? Observable.from(Collections.<DtlExternalLocation>emptyList()).toList().doOnNext(locations -> cleanCache())
                : Observable.empty();
    }

    private Observable<List<DtlExternalLocation>> apiSearch() {
        return shouldPerformApiSearch()
                ? rxApiFactory.composeApiCall(() -> this.dtlApi.searchLocations(query))
                .doOnNext(this::cacheInMemory)
                : Observable.empty();
    }

    private Observable<List<DtlExternalLocation>> localSearch() {
        return shouldPerformLocalSearch() || (shouldPerformApiSearch() && searchLocations != null)
                ? Observable.from(searchLocations).toList()
                : Observable.empty();
    }

    private void cleanCache() {
        searchLocations = null;
    }

    private void cacheInMemory(List<DtlExternalLocation> locations) {
        searchLocations = locations;
    }

    private Observable.Transformer<List<DtlExternalLocation>, List<DtlExternalLocation>> prepareLocations() {
        Comparator<DtlExternalLocation> comparator = DtlExternalLocation.provideComparator(query);
        return observable -> observable
                .compose(new ListFilter<>(dtlLocation -> dtlLocation.getLongName().toLowerCase().contains(query)))
                .compose(new ListSorter<>(comparator::compare));
    }

    private boolean shouldPerformEmptySearch() {
        return query.length() < API_SEARCH_QUERY_LENGHT;
    }

    private boolean shouldPerformApiSearch() {
        return query.length() == API_SEARCH_QUERY_LENGHT;
    }

    private boolean shouldPerformLocalSearch() {
        return query.length() > API_SEARCH_QUERY_LENGHT;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Persisted location
    ///////////////////////////////////////////////////////////////////////////

    private DtlLocation persistedLocation;
    private BehaviorSubject<DtlLocation> locationStream = BehaviorSubject.create();

    public Observable<DtlLocation> getLocationStream() {
        return locationStream.asObservable();
    }

    public void cleanLocation() {
        persistedLocation = null;
        db.cleanDtlLocation();
        locationStream.onNext(null);
    }

    public void persistLocation(DtlLocation location) {
        persistedLocation = location;
        db.saveDtlLocation(location);
        db.cleanLastMapCameraPosition(); // need clean last map camera position
        locationStream.onNext(location);
    }

    @Nullable
    public DtlLocation getSelectedLocation() {
        return persistedLocation;
    }

    @NonNull
    public DtlLocation getCachedSelectedLocation() {
        if (persistedLocation == null) {
            persistedLocation = db.getDtlLocation();
            locationStream.onNext(persistedLocation);
        }
        return persistedLocation;
    }

    // TODO :: 3/24/16 migrate to usage of methods below throughout DTL
    public boolean isLocationExternal() {
        return getCachedSelectedLocation().getLocationSourceType() == LocationSourceType.EXTERNAL;
    }

    public boolean isLocationFromMap() {
        return getCachedSelectedLocation().getLocationSourceType() == LocationSourceType.FROM_MAP;
    }

    public boolean isLocationNearMe() {
        return getCachedSelectedLocation().getLocationSourceType() == LocationSourceType.NEAR_ME;
    }
}
