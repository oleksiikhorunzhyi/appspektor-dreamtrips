package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
        implements DtlMerchantsPresenter {

    @Inject
    DtlFilterMerchantService filterService;
    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlLocationService locationService;
    //
    @State
    boolean initialized;

    public DtlMerchantsPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());

        getView().getToggleObservable()
                .skip(1)//skip emmit of initialization
                .subscribe(offersOnly -> filterService.filterDataPipe().send(DtlFilterDataAction.applyOffersOnly(offersOnly)));

        //
        connectService();
        connectFilterDataChanges();
        //
        if (!initialized) {
            locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                    .map(DtlLocationCommand::getResult)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(location ->
                            merchantService.merchantsActionPipe().send(DtlMerchantsAction.load(location.getCoordinates().asAndroidLocation()))
                    );
            initialized = true;
        }
        //
        if (!getView().isTabletLandscape())
            filterService.filterMerchantsActionPipe().observeSuccess()
                    .compose(bindViewIoToMainComposer())
                    .map(DtlFilterMerchantsAction::getResult)
                    .subscribe(this::tryRedirectToLocation);

        Observable.combineLatest(
                locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                        .map(DtlLocationCommand::getResult),
                filterService.getFilterData().map(DtlFilterData::getSearchQuery),
                Pair::new
        ).compose(bindViewIoToMainComposer())
                .take(1)
                .subscribe(pair -> {
                    getView().updateToolbarTitle(pair.first, pair.second);
                });

        filterService.getFilterData()
                .map(DtlFilterData::isOffersOnly)
                .subscribe(getView()::toggleDiningFilterSwitch);

    }

    private void connectFilterDataChanges() {
        filterService.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData -> {
                    getView().setFilterButtonState(!dtlFilterData.isDefault());
                });
    }

    private void connectService() {
        merchantService.merchantsActionPipe()//for progress
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress()));

        filterService.filterMerchantsActionPipe()//observe data
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> {
                            getView().hideProgress();
                            apiErrorPresenter.handleActionError(action, throwable);
                        })
                        .onSuccess(action -> getView().setItems(action.getResult())));
        //
        filterService.getFilterData()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
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
                locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
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
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()),
                merchant, null));
    }

    @Override
    public void onOfferClick(DtlOffer offer) {
        showOfferData(offer);
    }

    private void showOfferData(DtlOffer offer) {
        findMerchantWithOffer(offer)
                .filter(merchant -> merchant != null)
                .subscribe(merchant -> Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant, findExpandablePosition(merchant, offer))),
                        Throwable::printStackTrace);
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        getView().toggleSelection(event.getDtlMerchant());
    }

    //TODO bad hack!!! find better solution needed
    private Observable<DtlMerchant> findMerchantWithOffer(DtlOffer offer) {
        return merchantService.merchantsActionPipe().observeWithReplay()
                .first()
                .compose(new ActionStateToActionTransformer<>())
                .flatMap(action -> Observable.from(action.getCacheData()))
                .filter(merchant -> !merchant.hasNoOffers())
                .filter(merchant ->
                        Queryable.from(merchant.getOffers())
                                .filter(off -> off.equals(offer))
                                .any())
                .take(1);
    }

    protected List<Integer> findExpandablePosition(DtlMerchant merchant, DtlOffer... expandedOffers) {
        List<DtlOffer> merchantOffers = merchant.getOffers();
        return Queryable.from(Arrays.asList(expandedOffers))
                .filter(merchantOffers::contains)
                .map(merchantOffers::indexOf)
                .toList();
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
