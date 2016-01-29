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
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
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

    public final Job1Executor<Location, List<DtlLocation>> nearbyLocationExecutor =
            new Job1Executor<>(this::loadNearby);
    private Subscription nearbySubscription;

    public void loadNearbyLocations(Location userLocation) {
        nearbySubscription = nearbyLocationExecutor.createJobWith(userLocation).subscribe();
    }

    private Observable<List<DtlLocation>> loadNearby(Location location) {
        return rxApiFactory.composeApiCall(() ->
                this.dtlApi.getNearbyLocations(location.getLatitude() + ","
                        + location.getLongitude()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    private static final int API_SEARCH_QUERY_LENGHT = 3;

    public final Job0Executor<List<DtlLocation>> searchLocationExecutor = new Job0Executor<>(this::search);

    private Subscription searchSubscription;

    private String query;
    private List<DtlLocation> searchLocations;

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

    private Observable<List<DtlLocation>> search() {
        return Observable.concat(emptySearch(), localSearch(), apiSearch())
                .first()
                .compose(prepareLocations());
    }

    private Observable<List<DtlLocation>> emptySearch() {
        return shouldPerformEmptySearch()
                ? Observable.from(Collections.<DtlLocation>emptyList()).toList().doOnNext(locations -> cleanCache())
                : Observable.empty();
    }

    private Observable<List<DtlLocation>> apiSearch() {
        return shouldPerformApiSearch()
                ? rxApiFactory.composeApiCall(() -> this.dtlApi.searchLocations(query))
                .doOnNext(this::cacheInMemory)
                : Observable.empty();
    }

    private Observable<List<DtlLocation>> localSearch() {
        return shouldPerformLocalSearch() || (shouldPerformApiSearch() && searchLocations != null)
                ? Observable.from(searchLocations).toList()
                : Observable.empty();
    }

    private void cleanCache() {
        searchLocations = null;
    }

    private void cacheInMemory(List<DtlLocation> locations) {
        searchLocations = locations;
    }

    private Observable.Transformer<List<DtlLocation>, List<DtlLocation>> prepareLocations() {
        Comparator<DtlLocation> comparator = DtlLocation.provideComparator(query);
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

    public void cleanLocation() {
        persistedLocation = null;
        db.cleanDtlLocation();
    }

    public void persistLocation(DtlLocation location) {
        if (persistedLocation == null || !location.getId().equals(persistedLocation.getId())) {
            persistedLocation = location;
            db.saveDtlLocation(location);
        }
    }

    @Nullable
    public DtlLocation getSelectedLocation() {
        return persistedLocation;
    }

    @NonNull
    public DtlLocation getCachedSelectedLocation() {
        if (persistedLocation == null) persistedLocation = db.getDtlLocation();
        return persistedLocation;
    }
}
