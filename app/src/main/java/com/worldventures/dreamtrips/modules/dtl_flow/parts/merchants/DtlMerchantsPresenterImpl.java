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
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import techery.io.library.JobSubscriber;

public class DtlMerchantsPresenterImpl extends FlowPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
        implements DtlMerchantsPresenter {

    @Inject
    SnappyRepository db;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    private final PublishSubject<List<DtlMerchant>> merchantsStream = PublishSubject.create();
    private BehaviorSubject<Boolean> toggleStream;

    public DtlMerchantsPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        toggleStream = BehaviorSubject.create(db.getLastSelectedOffersOnlyToggle());
        bindMerchantManager();
        bindFilteredStream();
        dtlMerchantManager.loadMerchants(
                dtlLocationManager.getSelectedLocation().getCoordinates().asAndroidLocation());
        //
        if (!getView().isTabletLandscape())
        dtlMerchantManager.getMerchantsExecutor.connectSuccessOnly()
                .compose(bindViewIoToMainComposer())
                .subscribe(new JobSubscriber<List<DtlMerchant>>()
                        .onSuccess(this::tryRetirectToLocation));
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
        merchantsStream.asObservable().compose(bindViewIoToMainComposer())
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
        return toggleStream.asObservable()
                .compose(bindView())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // set initial value before emitting switching
                .doOnSubscribe(() ->
                        getView().toggleDiningFilterSwitch(db.getLastSelectedOffersOnlyToggle()))
                .doOnNext(st -> db.saveLastSelectedOffersOnlyToogle(st));
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Flow.get(getContext()).set(new DtlMapPath());
                return true;
            case R.id.action_dtl_filter:
                getView().openRightDrawer();
                return true;
        }
        return false;
    }

    private void tryRetirectToLocation(List<DtlMerchant> merchants) {
        if (merchants.isEmpty()) Flow.get(getContext()).set(new DtlLocationsPath(true));
    }

    public void applySearch(String query) {
        dtlMerchantManager.applySearch(query);
    }

    @Override
    public void merchantClicked(DtlMerchant merchant) {
        if (!TextUtils.isEmpty(dtlMerchantManager.getCurrentQuery()))
            TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                    dtlMerchantManager.getCurrentQuery(),
                    dtlLocationManager.getCachedSelectedLocation());
//        if (getView().isTabletLandscape())
//            getEventBus().post(new MerchantClickedEvent(merchant.getId())); // TODO :: 4/2/16 eventBus vs CellDelegate here
//        else Flow.get(getContext()).set(new DtlDetailsPath(merchant.getId()));
        Flow.get(getContext()).set(new DtlDetailsPath(merchant.getId()));
    }

    @Override
    public void onCheckHideDinings(boolean checked) {
        toggleStream.onNext(checked);
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
