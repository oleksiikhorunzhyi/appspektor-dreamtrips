package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantStore;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantStore;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import icepick.State;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.subjects.PublishSubject;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
        implements DtlMerchantsPresenter {

    @Inject
    SnappyRepository db;
    @Inject
    Janet janet;
    @Inject
    DtlFilterMerchantStore filteredMerchantStore;
    @Inject
    DtlMerchantStore merchantStore;
    @Inject
    DtlFilterMerchantStore filterStore;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    @State
    boolean initialized;
    //
    private final PublishSubject<List<DtlMerchant>> merchantsStream = PublishSubject.create();
    private final WriteActionPipe<DtlMerchantStoreAction> merchantStoreActionPipe;
    private final WriteActionPipe<DtlFilterMerchantStoreAction> filteredMerchantStoreActionPipe;

    public DtlMerchantsPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
        merchantStoreActionPipe = janet.createPipe(DtlMerchantStoreAction.class);
        filteredMerchantStoreActionPipe = janet.createPipe(DtlFilterMerchantStoreAction.class);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        getView().toggleDiningFilterSwitch(db.getLastSelectedOffersOnlyToggle());
        //
        bindMerchantStores();
        bindFilteredStream();
        bindFilterState();
        //
        if (!initialized) {
            dtlLocationManager.getSelectedLocation()
                    .map(DtlLocationCommand::getResult)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(location -> merchantStoreActionPipe.send(
                            DtlMerchantStoreAction.load(location.getCoordinates().asAndroidLocation())));
            initialized = true;
        }
        //
        if (!getView().isTabletLandscape())
            filteredMerchantStore.filteredMerchantsChangesPipe().observeSuccess()
                    .compose(bindViewIoToMainComposer())
                    .map(DtlFilterMerchantsAction::getResult)
                    .subscribe(this::tryRedirectToLocation);
        //
        filterStore.getFilterDataState()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                getView().setFilterButtonState(!dtlFilterData.isDefault()));
        //
        filteredMerchantStore.filteredMerchantsChangesPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> apiErrorPresenter.handleError(throwable)));
        //
        Observable.combineLatest(
                dtlLocationManager.getSelectedLocation().map(DtlLocationCommand::getResult),
                filteredMerchantStore.getFilterDataState().map(DtlFilterData::getSearchQuery),
                Pair::new
        ).compose(bindViewIoToMainComposer())
                .take(1)
                .subscribe(pair -> {
                    getView().updateToolbarTitle(pair.first, pair.second);
                });
    }

    private void bindFilteredStream() {
        final Observable<List<DtlMerchant>> merchantsStream =
                Observable.combineLatest(this.merchantsStream, prepareFilterToogle(),
                        this::filterMerchantsByType);
        merchantsStream.asObservable().compose(bindView())
                .subscribe(getView()::setItems);
    }

    private void bindFilterState() {
        filterStore.observeStateChange()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    private List<DtlMerchant> filterMerchantsByType(List<DtlMerchant> merchants, boolean hideDinings) {
        return Observable.from(merchants)
                .filter(merchant ->
                        !(hideDinings && merchant.getMerchantType() == DtlMerchantType.DINING))
                .toList().toBlocking().firstOrDefault(Collections.emptyList());
    }

    private void bindMerchantStores() {
        merchantStore.merchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress()));

        filteredMerchantStore.filteredMerchantsChangesPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> getView().hideProgress())
                        .onSuccess(action -> merchantsStream.onNext(action.getResult())));
    }

    private Observable<Boolean> prepareFilterToogle() {
        return getView().getToggleObservable()
                .startWith(db.getLastSelectedOffersOnlyToggle())
                .doOnNext(checked -> db.saveLastSelectedOffersOnlyToogle(checked));
    }

    @Override
    public void mapClicked() {
        History history = History.single(new DtlMapPath(FlowUtil.currentMaster(getContext()),
                getView().isToolbarCollapsed()));
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    private void tryRedirectToLocation(List<DtlMerchant> merchants) {
        if (!merchants.isEmpty())
            return; // TODO :: 4/14/16 also check applied filters number to be 0
        filteredMerchantStore.getFilterDataState()
                .map(DtlFilterData::getSearchQuery)
                .filter(TextUtils::isEmpty)
                .compose(bindViewIoToMainComposer())
                .subscribe(s ->
                        Flow.get(getContext()).set(DtlLocationsPath.builder()
                                .allowUserGoBack(true)
                                .showNoMerchantsCaption(true)
                                .build()));
    }

    @Override
    public void locationChangeRequested() {
        History history = History.single(new DtlLocationChangePath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    @Override
    public void applySearch(String query) {
        filteredMerchantStoreActionPipe.send(DtlFilterMerchantStoreAction.applySearch(query));
    }

    @Override
    public void merchantClicked(DtlMerchant merchant) {
        Observable.combineLatest(
                filteredMerchantStore.getFilterDataState()
                        .map(DtlFilterData::getSearchQuery),
                dtlLocationManager.getSelectedLocation()
                        .map(DtlLocationCommand::getResult),
                Pair::new
        ).compose(bindViewIoToMainComposer())
                .take(1)
                .subscribe(pair -> {
                    if (TextUtils.isEmpty(pair.first)) return;
                    TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                            pair.first,
                            pair.second);
                });
//        }
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant.getId(), null));
    }

    @Override
    public void perkClick(DtlOfferData perk) {
        showOfferData(perk);
    }

    @Override
    public void pointClicked(DtlOfferData points) {
        showOfferData(points);
    }

    private void showOfferData(DtlOfferData offer) {
        findMerchantId(offer)
                .filter(merchantId -> !TextUtils.isEmpty(merchantId))
                .subscribe(merchantId -> Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchantId, offer)),
                        Throwable::printStackTrace);
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        getView().toggleSelection(event.getDtlMerchant());
    }

    //TODO bad hack!!! find better solution needed
    private Observable<String> findMerchantId(DtlOfferData offer) {
        return merchantStore.getState()
                .compose(new ActionStateToActionTransformer<>())
                .flatMap(action -> Observable.from(action.getResult()))
                .filter(merchant -> !merchant.hasNoOffers())
                .filter(merchant ->
                        Queryable.from(merchant.getOffers())
                                .filter(off -> off.getOffer().equals(offer))
                                .any())
                .map(DtlMerchant::getId)
                .take(1);
    }

    @Override
    public void onDetachedFromWindow() {
        apiErrorPresenter.dropView();
        super.onDetachedFromWindow();
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_dtl_list;
    }
}
