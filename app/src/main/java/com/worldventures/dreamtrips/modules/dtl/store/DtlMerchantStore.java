package com.worldventures.dreamtrips.modules.dtl.store;

import android.location.Location;

import com.messenger.api.temp.RetryLoginComposer;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.janet.ResultOnlyFilter;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlPersistedMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateAmenitiesAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction.Action.CLEAN;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction.Action.LOAD;

public class DtlMerchantStore {

    @Inject
    SnappyRepository db;
    @Inject
    DtlApi dtlApi;
    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    Janet janet;

    private final ActionPipe<DtlUpdateAmenitiesAction> updateAmenitiesPipe;
    private final ActionPipe<DtlMerchantsAction> loadMerchantsPipe;
    private final ActionPipe<DtlPersistedMerchantsAction> persistMerchantsPipe;
    private final WriteActionPipe<DtlFilterMerchantStoreAction> filterStorePipe;

    private ActionState<DtlMerchantsAction> state;

    public DtlMerchantStore(Injector injector) {
        injector.inject(this);

        Janet privateJanet = new Janet.Builder()
                .addService(new CacheResultWrapper(new CommandActionService()))
                .build();
        updateAmenitiesPipe = privateJanet.createPipe(DtlUpdateAmenitiesAction.class, Schedulers.io());
        loadMerchantsPipe = privateJanet.createPipe(DtlMerchantsAction.class, Schedulers.io());
        persistMerchantsPipe = privateJanet.createPipe(DtlPersistedMerchantsAction.class, Schedulers.io());
        filterStorePipe = janet.createPipe(DtlFilterMerchantStoreAction.class);

        ReadActionPipe<DtlMerchantStoreAction> storePipe = janet.createPipe(DtlMerchantStoreAction.class);

        subscribeStoreActions(storePipe);
        connectMerchantsPipe();
        connectUpdateAmenitiesPipe();

    }

    private void subscribeStoreActions(ReadActionPipe<DtlMerchantStoreAction> storePipe) {
        storePipe.observeSuccess()
                .filter(action -> action.getResult() == LOAD)
                .subscribe(this::onLoadMerchants);

        storePipe.observeSuccess()
                .filter(action -> action.getResult() == CLEAN)
                .subscribe(this::onClear);
    }

    private void connectMerchantsPipe() {
        loadMerchantsPipe.observe()
                .compose(ResultOnlyFilter.instance())
                .doOnNext(actionState -> state = actionState)
                .compose(new ActionStateToActionTransformer<>())
                .subscribe(action -> {
                    db.saveDtlMerhants(action.getResult());
                    tryUpdateLocation(action.getResult());
                    updateAmenitiesPipe.send(new DtlUpdateAmenitiesAction(db, action.getResult()));
                }, Throwable::printStackTrace);
    }

    private void connectUpdateAmenitiesPipe() {
        updateAmenitiesPipe.observeSuccess()
                .subscribe(action ->
                        filterStorePipe.send(DtlFilterMerchantStoreAction.amenitiesUpdate(action.getResult())));
    }

    private void onLoadMerchants(DtlMerchantStoreAction action) {
        Location location = action.getLocation();
        String locationArg = String.format("%1$f,%2$f",
                location.getLatitude(), location.getLongitude());
        loadMerchantsPipe.createObservableSuccess(new DtlMerchantsAction(dtlApi, locationArg))
                .compose(new RetryLoginComposer<>(appSessionHolder, janet))
                .subscribe();
    }

    private void onClear(DtlMerchantStoreAction action) {
        loadMerchantsPipe.clearReplays();
        db.clearMerchantData();
        state = null;
    }

    private void tryUpdateLocation(List<DtlMerchant> dtlMerchants) {
        dtlLocationManager.getSelectedLocation()
                .filter(command -> {
                    LocationSourceType sourceType = command.getResult().getLocationSourceType();
                    return (sourceType == LocationSourceType.FROM_MAP || sourceType == LocationSourceType.NEAR_ME)
                            && !dtlMerchants.isEmpty();
                })
                .map(DtlLocationCommand::getResult)
                .subscribe(location -> {
                    DtlMerchant nearestMerchant = dtlMerchants.get(0);
                    DtlLocation updatedLocation = ImmutableDtlManualLocation
                            .copyOf((DtlManualLocation) location)
                            .withLongName(location.getLocationSourceType() == LocationSourceType.FROM_MAP
                                    ? nearestMerchant.getCity() : location.getLongName())
                            .withAnalyticsName(nearestMerchant.getAnalyticsName());
                    dtlLocationManager.persistLocation(updatedLocation);
                });
    }

    public Observable<ActionState<DtlMerchantsAction>> getState() {
        return Observable.just(state)
                .compose(new NonNullFilter<>())
                .switchIfEmpty(
                        getPersistedState()
                                .compose(ResultOnlyFilter.instance())
                                .flatMap(actionState -> {
                                    List<DtlMerchant> merchants = actionState.action.getResult();
                                    if (merchants == null) {
                                        merchants = Collections.emptyList();
                                    }
                                    return loadMerchantsPipe.createObservable(new DtlMerchantsAction(actionState.action.getResult()))
                                            .compose(ResultOnlyFilter.instance());
                                })
                );
    }

    public Observable<ActionState<DtlPersistedMerchantsAction>> getPersistedState() {
        return persistMerchantsPipe.createObservable(new DtlPersistedMerchantsAction(db));
    }

    public ReadActionPipe<DtlMerchantsAction> merchantsActionPipe() {
        return loadMerchantsPipe;
    }

    public Observable<DtlMerchant> getMerchantById(String merchantId) {
        return getState()
                .compose(new ActionStateToActionTransformer<>())
                .map(DtlMerchantsAction::getResult)
                .flatMap(Observable::from)
                .filter(merchant -> merchant.getId().equals(merchantId));
    }
}
