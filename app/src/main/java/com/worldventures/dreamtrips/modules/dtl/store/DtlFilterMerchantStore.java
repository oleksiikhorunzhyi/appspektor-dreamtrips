package com.worldventures.dreamtrips.modules.dtl.store;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterParameters;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.CommandActionBase;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.AMENITIES_UPDATE;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.APPLY_PARAMS;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.APPLY_SEARCH;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.INIT;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.RESET;

public class DtlFilterMerchantStore {

    @Inject
    SnappyRepository db;
    @Inject
    DtlMerchantStore dtlMerchantStore;
    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    Janet janet;

    private final ActionPipe<DtlFilterDataAction> filterDataPipe;
    private final ActionPipe<DtlFilterMerchantsAction> filterMerchantsPipe;


    public DtlFilterMerchantStore(Injector injector) {
        injector.inject(this);

        Janet privateJanet = new Janet.Builder()
                .addService(new CacheResultWrapper(new CommandActionService()))
                .build();
        filterMerchantsPipe = privateJanet.createPipe(DtlFilterMerchantsAction.class, Schedulers.io());
        filterDataPipe = privateJanet.createPipe(DtlFilterDataAction.class, Schedulers.io());

        initFilterData();
        subscribeStoreActions(janet.createPipe(DtlFilterMerchantStoreAction.class));
        connectFilterMerchantsPipe();
    }

    private void subscribeStoreActions(ReadActionPipe<DtlFilterMerchantStoreAction> storePipe) {
        storePipe.observeSuccess()
                .filter(action -> action.getResult() == INIT)
                .subscribe(action -> initFilterData());

        storePipe.observeSuccess()
                .filter(action -> action.getResult() == RESET)
                .subscribe(this::onReset);

        storePipe.observeSuccess()
                .filter(action -> action.getResult() == AMENITIES_UPDATE)
                .subscribe(this::onAmenitiesUpdate);

        storePipe.observeSuccess()
                .filter(action -> action.getResult() == APPLY_PARAMS)
                .subscribe(this::onApplyParams);

        storePipe.observeSuccess()
                .filter(action -> action.getResult() == APPLY_SEARCH)
                .subscribe(this::onApplySearch);
    }

    private void connectFilterMerchantsPipe() {
        observeStateChange()
                .subscribe(data -> {
                    filterMerchantsPipe.send(new DtlFilterMerchantsAction(data, dtlMerchantStore, dtlLocationManager, locationDelegate));
                });
    }

    private void initFilterData() {
        filterDataPipe.send(DtlFilterDataAction.update(
                data -> {
                    Setting distanceSetting = Queryable.from(db.getSettings()).filter(setting ->
                            setting.getName().equals(SettingsFactory.DISTANCE_UNITS)).firstOrDefault();
                    return ImmutableDtlFilterData.copyOf(data)
                            .withDistanceType(DistanceType.provideFromSetting(distanceSetting));
                }));
    }

    private void onReset(DtlFilterMerchantStoreAction action) {
        filterDataPipe.send(DtlFilterDataAction.update(
                data -> DtlFilterData.merge(
                        ImmutableDtlFilterParameters.builder()
                                .selectedAmenities(db.getAmenities())
                                .build(),
                        data)));
    }

    private void onAmenitiesUpdate(DtlFilterMerchantStoreAction action) {
        filterDataPipe.send(DtlFilterDataAction.update(
                data -> ImmutableDtlFilterData
                        .copyOf(data)
                        .withAmenities(action.getAmenities())
                        .withSelectedAmenities(action.getAmenities())));
    }

    private void onApplyParams(DtlFilterMerchantStoreAction action) {
        filterDataPipe.send(DtlFilterDataAction.update(
                data -> {
                    data = DtlFilterData.merge(action.getFilterParameters(), data);
                    TrackingHelper.dtlMerchantFilter(data);
                    return data;
                }));
    }

    private void onApplySearch(DtlFilterMerchantStoreAction action) {
        filterDataPipe.send(DtlFilterDataAction.update(
                data -> ImmutableDtlFilterData.copyOf(data)
                        .withSearchQuery(action.getQuery())));
    }

    public Observable<DtlFilterData> observeStateChange() {
        return filterDataPipe.observeSuccess()
                .filter(DtlFilterDataAction::withUpdateFunc)
                .map(CommandActionBase::getResult);
    }

    public Observable<DtlFilterData> getFilterDataState() {
        return filterDataPipe.createObservableSuccess(DtlFilterDataAction.read())
                .map(CommandActionBase::getResult);
    }

    public ReadActionPipe<DtlFilterMerchantsAction> filteredMerchantsChangesPipe() {
        return filterMerchantsPipe;
    }

}
