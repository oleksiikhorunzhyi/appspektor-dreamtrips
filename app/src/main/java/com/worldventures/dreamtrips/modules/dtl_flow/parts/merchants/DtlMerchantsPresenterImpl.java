package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantService;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationService;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantService;
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
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.subjects.PublishSubject;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
        implements DtlMerchantsPresenter {

    @Inject
    SnappyRepository db;
    @Inject
    DtlFilterMerchantService filterService;
    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlLocationService locationService;
    //
    @State
    boolean initialized;
    //
    private final PublishSubject<List<DtlMerchant>> merchantsStream = PublishSubject.create();

    public DtlMerchantsPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
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
            locationService.locationPipe().createObservableSuccess(DtlLocationCommand.get())
                    .map(DtlLocationCommand::getResult)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(location -> merchantService.loadMerchants(location.getCoordinates().asAndroidLocation()));
            initialized = true;
        }
        //
        if (!getView().isTabletLandscape())
            filterService.filterMerchantsActionPipe().observeSuccess()
                    .compose(bindViewIoToMainComposer())
                    .map(DtlFilterMerchantsAction::getResult)
                    .subscribe(this::tryRedirectToLocation);
        //
        filterService.getFilterData()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
        //
        filterService.filterMerchantsActionPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> apiErrorPresenter.handleError(throwable)));

        merchantService.merchantsActionPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onFail(apiErrorPresenter::handleActionError));
        //
        Observable.combineLatest(
                locationService.locationPipe().createObservableSuccess(DtlLocationCommand.get())
                        .map(DtlLocationCommand::getResult),
                filterService.getFilterData().map(DtlFilterData::getSearchQuery),
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
        filterService.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
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
        merchantService.merchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress()));

        filterService.filterMerchantsActionPipe()
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
        filterService.getFilterData()
                .compose(bindViewIoToMainComposer())
                .filter(dtlFilterData -> merchants.isEmpty())
                .filter(dtlFilterData -> TextUtils.isEmpty(dtlFilterData.getSearchQuery()))
                .filter(DtlFilterData::isDefault)
                .subscribe(dtlFilterData ->
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
        filterService.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
    }

    @Override
    public void merchantClicked(DtlMerchant merchant) {
        Observable.combineLatest(
                filterService.getFilterData()
                        .map(DtlFilterData::getSearchQuery),
                locationService.locationPipe().createObservableSuccess(DtlLocationCommand.get())
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
        return merchantService.merchantsActionPipe().observeWithReplay()
                .first()
                .compose(new ActionStateToActionTransformer<>())
                .flatMap(action -> Observable.from(action.getCacheData()))
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
