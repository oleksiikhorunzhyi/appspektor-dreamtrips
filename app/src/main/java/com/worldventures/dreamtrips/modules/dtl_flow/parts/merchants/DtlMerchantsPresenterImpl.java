package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.locations.DtlLocationsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import techery.io.library.JobSubscriber;

public class DtlMerchantsPresenterImpl extends FlowPresenterImpl<DtlMerchantsScreen, ViewState.EMPTY>
        implements DtlMerchantsPresenter {

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;

    public DtlMerchantsPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        bindMerchantManager();
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

    private void bindMerchantManager() {
        dtlMerchantManager.connectMerchantsWithCache()
                .compose(bindViewIoToMainComposer())
                .subscribe(new JobSubscriber<List<DtlMerchant>>()
                        .onSuccess(getView()::setItems)
                        .onProgress(getView()::showProgress)
                        .onError(thr -> getView().hideProgress()));
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
