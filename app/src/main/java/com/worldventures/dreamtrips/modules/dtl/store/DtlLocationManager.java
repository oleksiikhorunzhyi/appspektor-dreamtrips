package com.worldventures.dreamtrips.modules.dtl.store;

import android.content.Context;
import android.location.Location;
import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlNearbyLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlSearchLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionPipe;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.CommandActionBase;
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

    private ActionPipe<DtlNearbyLocationCommand> nearbyLocationPipe;
    private ActionPipe<DtlSearchLocationCommand> searchLocationPipe;
    private ActionPipe<DtlUpdateLocationCommand> updateLocationPipe;
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
                .addService(new SearchLocationWrapper(new CacheLocationWrapper(new CommandActionService()), dtlApi))
                .build();
        nearbyLocationPipe = janet.createPipe(DtlNearbyLocationCommand.class, scheduler);
        searchLocationPipe = janet.createPipe(DtlSearchLocationCommand.class, scheduler);
        updateLocationPipe = janet.createPipe(DtlUpdateLocationCommand.class, scheduler);
        locationPipe = janet.createPipe(DtlLocationCommand.class, scheduler);
        locationPipe.send(new DtlLocationCommand(db));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nearby
    ///////////////////////////////////////////////////////////////////////////


    public ReadActionPipe<DtlNearbyLocationCommand> nearbyLocationPipe() {
        return nearbyLocationPipe.asReadOnly();
    }

    public void loadNearbyLocations(Location location) {
        nearbyLocationPipe.send(new DtlNearbyLocationCommand(dtlApi, location));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    public ReadActionPipe<DtlSearchLocationCommand> searchLocationPipe() {
        return searchLocationPipe.asReadOnly();
    }

    public void searchLocations(String query) {
        nearbyLocationPipe.cancelLatest();
        searchLocationPipe.cancelLatest();
        searchLocationPipe.send(DtlSearchLocationCommand.createEmpty(query));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Persisted location
    ///////////////////////////////////////////////////////////////////////////


    public void cleanLocation() {
        locationPipe.send(new DtlLocationCommand(DtlLocation.UNDEFINED));
        updateLocationPipe.send(new DtlUpdateLocationCommand(db, DtlLocation.UNDEFINED));
    }

    public void persistLocation(DtlLocation location) {
        locationPipe.send(new DtlLocationCommand(location));
        updateLocationPipe.send(new DtlUpdateLocationCommand(db, location));
    }

    public Observable<DtlLocationCommand> getSelectedLocation() {
        return locationPipe.createObservableSuccess(new DtlLocationCommand(db));
    }

    public Observable<DtlLocation> observeLocationUpdates() {
        return updateLocationPipe.observeSuccess().map(CommandActionBase::getResult);
    }

    private final static class CacheLocationWrapper extends ActionServiceWrapper {

        private DtlLocation lastLocation;

        public CacheLocationWrapper(CommandActionService actionService) {
            super(actionService);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
            if (holder.action() instanceof DtlLocationCommand) {
                if (((DtlLocationCommand) holder.action()).isFromDB()
                        && lastLocation != null
                        && lastLocation.getLocationSourceType() != LocationSourceType.UNDEFINED) {
                    holder.newAction((A) new DtlLocationCommand(lastLocation));
                }
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

        @Override
        protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
            if (holder.action() instanceof DtlLocationCommand) {
                lastLocation = ((DtlLocationCommand) holder.action()).getResult();
            }
        }

        @Override
        protected <A> void onInterceptFail(ActionHolder<A> holder, JanetException e) {
        }
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
            if (holder.action() instanceof DtlSearchLocationCommand) {
                A newAction;
                String query = ((DtlSearchLocationCommand) holder.action()).getQuery();
                if (query.length() < API_SEARCH_QUERY_LENGTH) {
                    newAction = (A) DtlSearchLocationCommand.createEmpty(query);
                    cache = null;
                } else if (cache != null && query.toLowerCase().startsWith(cache.first)) {
                    newAction = (A) DtlSearchLocationCommand.createWith(filter(cache.second, query), query);
                } else {
                    String apiQuery = query.substring(0, API_SEARCH_QUERY_LENGTH);
                    newAction = (A) DtlSearchLocationCommand.createApiSearch(api, apiQuery, query);
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
            if (holder.action() instanceof DtlSearchLocationCommand) {
                DtlSearchLocationCommand action = (DtlSearchLocationCommand) holder.action();
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
