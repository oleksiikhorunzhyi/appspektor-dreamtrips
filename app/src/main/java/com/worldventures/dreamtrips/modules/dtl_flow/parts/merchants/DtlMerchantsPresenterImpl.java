package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
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
        dtlMerchantManager.loadMerchants(
                dtlLocationManager.getSelectedLocation().getCoordinates().asAndroidLocation());
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
        dtlLocationManager.getLocationStream()
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarTitle);
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
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Flow.get(getContext()).set(new DtlMapPath(FlowUtil.currentMaster(getContext())));
                return true;
            case R.id.action_dtl_filter:
                getView().openRightDrawer();
                return true;
        }
        return false;
    }

    private void tryRedirectToLocation(List<DtlMerchant> merchants) {
        if (merchants.isEmpty()) navigateToLocations();
    }

    @Override
    public void onToolbarTitleClicked() {
        navigateToLocations();
    }

    protected void navigateToLocations() {
            Flow.get(getContext()).set(DtlLocationsPath.builder()
                    .allowUserGoBack(true)
                    .showNoMerchantsCaption(true)
                    .build());
    }

    @Override
    public void applySearch(String query) {
        dtlMerchantManager.applySearch(query);
    }

    @Override
    public void merchantClicked(DtlMerchant merchant) {
        if (!TextUtils.isEmpty(dtlMerchantManager.getCurrentQuery())) {
            TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                    dtlMerchantManager.getCurrentQuery(),
                    dtlLocationManager.getCachedSelectedLocation());
        }
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant.getId()));
    }

    public void onEventMainThread(ToggleMerchantSelectionEvent event) {
        getView().toggleSelection(event.getDtlMerchant());
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
