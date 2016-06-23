package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.support.annotation.Nullable;
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
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
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
    DtlFilterMerchantInteractor filterInteractor;
    @Inject
    DtlMerchantInteractor merchantInteractor;
    @Inject
    DtlLocationInteractor locationInteractor;
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
        //
        getView().getToggleObservable()
                .subscribe(offersOnly -> filterInteractor.filterDataPipe()
                        .send(DtlFilterDataAction.applyOffersOnly(offersOnly)));
        //
        connectService();
        connectFilterDataChanges();
        //
        locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
                .filter(command -> !this.initialized)
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> {
                            merchantInteractor.merchantsActionPipe()
                                    .send(DtlMerchantsAction.load(
                                            location.getCoordinates().asAndroidLocation()));
                            initialized = true;
                        }
                );
        //
        merchantInteractor.merchantsActionPipe()
                .observe()
                .compose(bindViewIoToMainComposer())
                .compose(new ActionStateToActionTransformer<>())
                .filter(action -> !getView().isTabletLandscape())
                .filter(dtlMerchantsAction -> dtlMerchantsAction.getResult().isEmpty())
                .subscribe(s -> redirectToLocations(), e -> {});
        //
        locationInteractor.locationPipe().observeSuccessWithReplay()
                .first()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarLocationTitle);
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .compose(bindViewIoToMainComposer())
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::getSearchQuery)
                .subscribe(getView()::updateToolbarSearchCaption);
        //
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::isOffersOnly)
                .subscribe(getView()::toggleDiningFilterSwitch);
        //
        bindToolbarLocationCaptionUpdates();
    }

    private void bindToolbarLocationCaptionUpdates() {
        locationInteractor.locationPipe().observeSuccess()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarLocationTitle);
    }

    private void connectFilterDataChanges() {
        filterInteractor.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData -> {
                    getView().setFilterButtonState(!dtlFilterData.isDefault());
                });
    }

    private void connectService() {
        merchantInteractor.merchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress())
                        .onFail(apiErrorPresenter::handleActionError));

        filterInteractor.filterMerchantsActionPipe()//observe data
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> {
                            getView().hideProgress();
                            apiErrorPresenter.handleActionError(action, throwable);
                        })
                        .onSuccess(action -> getView().setItems(action.getResult())));
    }

    @Override
    public void mapClicked() {
        History history = History.single(new DtlMapPath(FlowUtil.currentMaster(getContext()),
                getView().isToolbarCollapsed()));
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    private void redirectToLocations() {
        Flow.get(getContext()).set(DtlLocationsPath.builder()
                .allowUserGoBack(true)
                .showNoMerchantsCaption(true)
                .build());
    }

    @Override
    public void locationChangeRequested() {
        History history = History.single(new DtlLocationChangePath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    @Override
    public void applySearch(String query) {
        filterInteractor.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
    }

    @Override
    public void merchantClicked(DtlMerchant merchant) {
        Observable.combineLatest(
                filterInteractor.filterDataPipe().observeSuccessWithReplay()
                        .first()
                        .map(DtlFilterDataAction::getResult)
                        .map(DtlFilterData::getSearchQuery),
                locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
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
        navigateToDetails(merchant, null);
    }

    private void navigateToDetails(DtlMerchant dtlMerchant, @Nullable DtlOffer dtlOffer) {
        if (Flow.get(getContext()).getHistory().size() < 2) {
            Flow.get(getContext()).set(new DtlMerchantDetailsPath(
                    FlowUtil.currentMaster(getContext()), dtlMerchant, dtlOffer == null ? null :
                    findExpandablePosition(dtlMerchant, dtlOffer)));
        } else {
            History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
            historyBuilder.pop();
            historyBuilder.push(new DtlMerchantDetailsPath(
                    FlowUtil.currentMaster(getContext()), dtlMerchant, dtlOffer == null ? null :
                    findExpandablePosition(dtlMerchant, dtlOffer)));
            Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
        }
    }

    @Override
    public void onOfferClick(DtlMerchant dtlMerchant, DtlOffer offer) {
        navigateToDetails(dtlMerchant, offer);
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        getView().toggleSelection(event.getDtlMerchant());
    }

    protected List<Integer> findExpandablePosition(DtlMerchant merchant,
                                                   DtlOffer... expandedOffers) {
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
}
