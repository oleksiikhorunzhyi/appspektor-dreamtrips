package com.worldventures.dreamtrips.modules.dtl.store;

import android.annotation.SuppressLint;
import android.location.Location;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.janet.ResultOnlyFilter;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.janet.cache.storage.Storage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.rx.composer.StubSubscriber;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateAmenitiesAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

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
    RetryLoginComposer retryLoginComposer;
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
    private final ActionPipe<DtlMerchantsAction> merchantsPipe;
    private final WriteActionPipe<DtlFilterMerchantStoreAction> filterStorePipe;

    private volatile ActionState<DtlMerchantsAction> state;

    public DtlMerchantStore(Injector injector) {
        injector.inject(this);

        Janet privateJanet = new Janet.Builder()
                .addService(new CacheResultWrapper(new CommandActionService())
                        .bindStorage(DtlMerchantsAction.class, new MerchantsCacheStorage(db)))
                .build();

        updateAmenitiesPipe = privateJanet.createPipe(DtlUpdateAmenitiesAction.class, Schedulers.io());
        merchantsPipe = privateJanet.createPipe(DtlMerchantsAction.class);
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
        merchantsPipe.observe()
                .compose(ResultOnlyFilter.instance())
                .doOnNext(actionState -> state = actionState)
                .compose(new ActionStateToActionTransformer<>())
                .filter(DtlMerchantsAction::isFromApi)
                .subscribe(action -> {
                    tryUpdateLocation(action.getResult());
                    updateAmenitiesPipe.send(new DtlUpdateAmenitiesAction(db, action.getResult()));
                }, Throwable::printStackTrace);
    }

    private void connectUpdateAmenitiesPipe() {
        updateAmenitiesPipe.observeSuccess()
                .subscribe(action ->
                        filterStorePipe.send(DtlFilterMerchantStoreAction.amenitiesUpdate(action.getResult())));
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("unchecked")
    private void onLoadMerchants(DtlMerchantStoreAction action) {
        Location location = action.getLocation();
        String locationArg = String.format("%1$f,%2$f",
                location.getLatitude(), location.getLongitude());
        merchantsPipe.createObservableSuccess(DtlMerchantsAction.fromApi(dtlApi, locationArg))
                .subscribeOn(Schedulers.io())
                .compose(retryLoginComposer)
                .subscribe(new StubSubscriber());
    }

    private void onClear(DtlMerchantStoreAction action) {
        merchantsPipe.clearReplays();
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
                        merchantsPipe.createObservable(DtlMerchantsAction.fromCache())
                                .compose(ImmediateComposer.instance())
                                .compose(ResultOnlyFilter.instance())
                );
    }

    public ReadActionPipe<DtlMerchantsAction> merchantsActionPipe() {
        return merchantsPipe;
    }

    public Observable<DtlMerchant> getMerchantById(String merchantId) {
        return getState()
                .compose(new ActionStateToActionTransformer<>())
                .map(DtlMerchantsAction::getResult)
                .flatMap(Observable::from)
                .filter(merchant -> merchant.getId().equals(merchantId));
    }

    private final static class MerchantsCacheStorage implements Storage<List<DtlMerchant>> {
        private final SnappyRepository db;

        private MerchantsCacheStorage(SnappyRepository db) {
            this.db = db;
        }

        @Override
        public void save(List<DtlMerchant> data) {
            db.saveDtlMerhants(data);
        }

        @Override
        public List<DtlMerchant> get() {
            return db.getDtlMerchants();
        }
    }
}
