package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerkData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
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
import rx.Observable;
import rx.subjects.PublishSubject;
import techery.io.library.JobSubscriber;

public class DtlMerchantsPresenterImpl extends DtlPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
        implements DtlMerchantsPresenter {

    @Inject
    SnappyRepository db;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
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
        bindMerchantManager();
        bindFilteredStream();
        //
        if (!initialized) {
            dtlLocationManager.getSelectedLocation()
                    .map(DtlLocationCommand::getResult)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(location -> dtlMerchantManager.loadMerchants(
                            location.getCoordinates().asAndroidLocation()));
            initialized = true;
        }
        //
        if (!getView().isTabletLandscape())
            dtlMerchantManager.getMerchantsExecutor.connectSuccessOnly()
                    .compose(bindViewIoToMainComposer())
                    .subscribe(new JobSubscriber<List<DtlMerchant>>()
                            .onSuccess(this::tryRedirectToLocation));
        //
        dtlMerchantManager.connectMerchantsWithCache()
                .compose(bindViewIoToMainComposer())
                .subscribe(new JobSubscriber<List<DtlMerchant>>()
                        .onError(apiErrorPresenter::handleError));
        //
        dtlLocationManager.getSelectedLocation()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> getView().updateToolbarTitle(location,
                        dtlMerchantManager.getCurrentQuery()));
    }

    private void bindFilteredStream() {
        final Observable<List<DtlMerchant>> merchantsStream =
                Observable.combineLatest(this.merchantsStream, prepareFilterToogle(),
                        this::filterMerchantsByType);
        merchantsStream.asObservable().compose(bindView())
                .subscribe(getView()::setItems);
    }

    private List<DtlMerchant> filterMerchantsByType(List<DtlMerchant> merchants, boolean hideDinings) {
        return Observable.from(merchants)
                .filter(merchant ->
                        !(hideDinings && merchant.getMerchantType() == DtlMerchantType.DINING))
                .toList().toBlocking().firstOrDefault(Collections.emptyList());
    }

    private void bindMerchantManager() {
        dtlMerchantManager.connectMerchantsWithCache()
                .compose(bindViewIoToMainComposer())
                .subscribe(new JobSubscriber<List<DtlMerchant>>()
                        .onSuccess(this.merchantsStream::onNext)
                        .onProgress(getView()::showProgress)
                        .onError(thr -> getView().hideProgress()));
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
        if (merchants.isEmpty() && // TODO :: 4/14/16 also check applied filters number to be 0
                TextUtils.isEmpty(dtlMerchantManager.getCurrentQuery()))
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
        dtlMerchantManager.applySearch(query);
    }

    @Override
    public void merchantClicked(DtlMerchant merchant) {
        if (!TextUtils.isEmpty(dtlMerchantManager.getCurrentQuery())) {
            dtlLocationManager.getSelectedLocation()
                    .map(DtlLocationCommand::getResult)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(location -> {
                        TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                                dtlMerchantManager.getCurrentQuery(),
                                location);
                    });
        }
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant.getId(), null));
    }

    @Override
    public void perkClick(DtlOfferData perk) {
        String merchantId = findMerchantId(perk);
        if (!TextUtils.isEmpty(merchantId))
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchantId, perk));
    }

    @Override
    public void pointClicked(DtlOfferData points) {
        String merchantId = findMerchantId(points);
        if (!TextUtils.isEmpty(merchantId))
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchantId, points));
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        getView().toggleSelection(event.getDtlMerchant());
    }

    //TODO bad hack!!! find better solution needed
    private String findMerchantId(DtlOfferData offer) {
        List<DtlMerchant> merchants = dtlMerchantManager.getMerchants();

        for (DtlMerchant merchant : merchants) {
            if(!merchant.hasNoOffers()) {
                DtlOffer dtlOffer = Queryable.from(merchant.getOffers()).filter(off -> off.getOffer().equals(offer)).firstOrDefault();
                if(dtlOffer != null) return merchant.getId();
            }
        }
        return null;
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
