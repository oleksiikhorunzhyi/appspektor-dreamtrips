package com.worldventures.dreamtrips.modules.dtl.store;

import android.content.Context;
import android.location.Location;
import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.StubSubscriber;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateLocationAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionPipe;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.JanetException;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class DtlLocationManager {

    @Inject
    DtlApi dtlApi;
    @Inject
    SnappyRepository db;
    @ForApplication
    @Inject
    Context context;
    @Inject
    RetryLoginComposer retryLoginComposer;

    private ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe;
    private ActionPipe<DtlSearchLocationAction> searchLocationPipe;
    private ActionPipe<DtlUpdateLocationAction> updateLocationPipe;
    private ActionPipe<DtlLocationCommand> locationPipe;

    public DtlLocationManager(Injector injector) {
        injector.inject(this);
        init(Schedulers.io());
    }

    public DtlLocationManager(DtlApi dtlApi, SnappyRepository db, Scheduler scheduler) {
        this.dtlApi = dtlApi;
        this.db = db;
        init(scheduler);
    }

    private void init(Scheduler scheduler) {
        Janet janet = new Janet.Builder()
                .addService(new SearchLocationWrapper(new CacheResultWrapper(new CommandActionService()), dtlApi))
                .build();
        nearbyLocationPipe = janet.createPipe(DtlNearbyLocationAction.class, scheduler);
        searchLocationPipe = janet.createPipe(DtlSearchLocationAction.class, scheduler);
        updateLocationPipe = janet.createPipe(DtlUpdateLocationAction.class, scheduler);
        locationPipe = janet.createPipe(DtlLocationCommand.class, scheduler);
        locationPipe.send(new DtlLocationCommand(db));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nearby
    ///////////////////////////////////////////////////////////////////////////


    public ReadActionPipe<DtlNearbyLocationAction> nearbyLocationPipe() {
        return nearbyLocationPipe.asReadOnly();
    }

    @SuppressWarnings("unchecked")
    public void loadNearbyLocations(Location location) {
        nearbyLocationPipe.createObservableSuccess(new DtlNearbyLocationAction(dtlApi, location))
                .compose(retryLoginComposer)
                .subscribe(new StubSubscriber<>());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    public ReadActionPipe<DtlSearchLocationAction> searchLocationPipe() {
        return searchLocationPipe.asReadOnly();
    }

    @SuppressWarnings("unchecked")
    public void searchLocations(String query) {
        nearbyLocationPipe.cancelLatest();
        searchLocationPipe.cancelLatest();
        searchLocationPipe.createObservableSuccess(DtlSearchLocationAction.createEmpty(query))
                .compose(retryLoginComposer)
                .subscribe(new StubSubscriber<>());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Persisted location
    ///////////////////////////////////////////////////////////////////////////


    public void cleanLocation() {
        locationPipe.send(new DtlLocationCommand(DtlLocation.UNDEFINED));
        updateLocationPipe.send(new DtlUpdateLocationAction(db, DtlLocation.UNDEFINED));
    }

    public void persistLocation(DtlLocation location) {
        locationPipe.send(new DtlLocationCommand(location));
        updateLocationPipe.send(new DtlUpdateLocationAction(db, location));
    }

    public Observable<DtlLocationCommand> getSelectedLocation() {
        return locationPipe.createObservableSuccess(new DtlLocationCommand(db));
    }

    private final static class SearchLocationWrapper extends ActionServiceWrapper {

        private static final int API_SEARCH_QUERY_LENGTH = 3;

        private final DtlApi api;

        private volatile Pair<String, List<DtlExternalLocation>> cache;

        public SearchLocationWrapper(ActionService actionService, DtlApi api) {
            super(actionService);
            this.api = api;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
            if (holder.action() instanceof DtlSearchLocationAction) {
                A newAction;
                String query = ((DtlSearchLocationAction) holder.action()).getQuery();
                if (query.length() < API_SEARCH_QUERY_LENGTH) {
                    newAction = (A) DtlSearchLocationAction.createEmpty(query);
                    cache = null;
                } else if (cache != null && query.toLowerCase().startsWith(cache.first)) {
                    newAction = (A) DtlSearchLocationAction.createWith(filter(cache.second, query), query);
                } else {
                    String apiQuery = query.substring(0, API_SEARCH_QUERY_LENGTH);
                    newAction = (A) DtlSearchLocationAction.createApiSearch(api, apiQuery, query);
                }
                holder.newAction(newAction);
            }
            return false;
        }

        @Override
        protected <A> void onInterceptCancel(ActionHolder<A> holder) {
        }

        @Override
        protected <A> void onInterceptStart(ActionHolder<A> holder) {
        }

        @Override
        protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
            if (holder.action() instanceof DtlSearchLocationAction) {
                DtlSearchLocationAction action = (DtlSearchLocationAction) holder.action();
                if (action.isFromApi()) {
                    String query = action.getQuery().toLowerCase();
                    String key = query.substring(0, Math.min(query.length(), API_SEARCH_QUERY_LENGTH));
                    cache = new Pair<>(key, new ArrayList<>(action.getResult()));
                    if (query.length() > key.length()) {
                        action.getResult().clear();
                        action.getResult().addAll(filter(cache.second, query));
                    }
                }
            }
        }

        private static List<DtlExternalLocation> filter(List<DtlExternalLocation> result, String query) {
            return Queryable.from(result)
                    .filter((element, index) -> element.getLongName().toLowerCase().contains(query.toLowerCase()))
                    .sort(DtlExternalLocation.provideComparator(query))
                    .toList();
        }

        @Override
        protected <A> void onInterceptFail(ActionHolder<A> holder, JanetException e) {
        }
    }
}
