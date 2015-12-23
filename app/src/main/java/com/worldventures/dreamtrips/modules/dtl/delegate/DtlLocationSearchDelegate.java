package com.worldventures.dreamtrips.modules.dtl.delegate;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.api.location.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class DtlLocationSearchDelegate {

    private static final int SEARCH_SYMBOL_COUNT = 3;

    private Listener listener;
    private RequestingPresenter requestingPresenter;
    //
    @State
    String query;
    @State
    ArrayList<DtlLocation> searchLocations;
    @State
    ArrayList<DtlLocation> nearbyLocations;
    //
    private GetDtlLocationsQuery getDtlLocationsQuery;
    private Subscription subscription;

    public DtlLocationSearchDelegate() {
        this.requestingPresenter = requestingPresenter;
        searchLocations = new ArrayList<>();
        nearbyLocations = new ArrayList<>();
    }

    ///////////////////////////////////////////////////////////////////////////
    // State saving/restoring
    ///////////////////////////////////////////////////////////////////////////

    public void saveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
    }

    public void restoreInstanceState(Bundle savedState) {
        Icepick.restoreInstanceState(this, savedState);
    }

    public void requestSavedSearchResults() {
        if (searchLocations.isEmpty()) provideResults(searchLocations);
        else localSearch();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search public
    ///////////////////////////////////////////////////////////////////////////

    public void setRequestingPresenter(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    public void setNearbyLocations(List<DtlLocation> locations) {
        this.nearbyLocations.addAll(locations);
    }

    public void performSearch(String query) {
        if (query.length() < SEARCH_SYMBOL_COUNT) {
            this.query = query;
            flushSearch();
            return;
        }

        int oldLength = this.query != null ? this.query.length() : 0;

        this.query = query;

        boolean apiSearch = oldLength < this.query.length() &&
                this.query.length() == SEARCH_SYMBOL_COUNT;

        if (apiSearch) apiSearch();
        else localSearch();
    }

    public void dismissDelegate() {
        tryDismissQuery();
        tryUnsubscribe();
        provideResults(nearbyLocations);
        searchLocations.clear();
        nearbyLocations.clear();
        query = "";
    }

    public void unsetRequestingPresenter() {
        requestingPresenter = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search private
    ///////////////////////////////////////////////////////////////////////////

    private void flushSearch() {
        searchLocations.clear();
        provideResults(searchLocations);
    }

    private void tryDismissQuery() {
        if (getDtlLocationsQuery != null) getDtlLocationsQuery.cancel();
    }

    private void tryUnsubscribe() {
        if (subscription != null &&
                !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

    private void apiSearch() {
        if (listener != null) listener.onSearchStarted();

        tryDismissQuery();
        getDtlLocationsQuery = new GetDtlLocationsQuery(query);

        checkState();
        requestingPresenter.doRequest(getDtlLocationsQuery, this::onSearchResultLoaded, this::onApiSearchFailed);
    }

    private void onSearchResultLoaded(ArrayList<DtlLocation> searchLocations) {
        this.searchLocations = searchLocations;
        localSearch();
    }

    private void localSearch() {
        if (!searchLocations.isEmpty())
            subscription = Observable.from(Queryable
                    .from(searchLocations)
                    .filter(dtlLocation ->
                            dtlLocation.getLongName().toLowerCase().contains(query))
                    .sort(new DtlLocation.DtlLocationRangeComparator(query))
                    .toList())
            .toList().compose(new IoToMainComposer<>())
            .subscribe(this::provideResults, e -> Timber.e(e, "Smth went wrong while search"));
    }

    private void onApiSearchFailed(SpiceException spiceException) {
        if (listener != null) listener.onSearchError(spiceException);
    }

    private void provideResults(List<DtlLocation> locations) {
        if (listener != null) listener.onSearchFinished(locations);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    public void attachListener(Listener listener) {
        this.listener = listener;
    }

    public void detachListener() {
        this.listener = null;
    }

    public interface Listener {

        void onSearchStarted();

        void onSearchFinished(List<DtlLocation> locations);

        void onSearchError(SpiceException e);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Check state methods
    ///////////////////////////////////////////////////////////////////////////

    protected void checkState() {
        if (requestingPresenter == null)
            throw new IllegalStateException("You should set RequestingPresenter before loading anything");
    }
}
