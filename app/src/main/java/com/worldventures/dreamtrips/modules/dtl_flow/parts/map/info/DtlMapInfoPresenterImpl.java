package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

import javax.inject.Inject;

import flow.Flow;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    protected DtlMerchant merchant;

    public DtlMapInfoPresenterImpl(Context context, Injector injector, String merchantId) {
        super(context);
        injector.inject(this);
        this.merchant = dtlMerchantManager.getMerchantById(merchantId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //
        getView().setMerchant(merchant);
    }

    public void onEvent(DtlShowMapInfoEvent event) {
        getView().visibleLayout(true);
    }


    @Override
    public void onMarkerClick() {
        eventBus.post(new ToggleMerchantSelectionEvent(merchant));
        trackIfNeeded();
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()), merchant.getId(), null));
    }

    private void trackIfNeeded() {
        if (!TextUtils.isEmpty(dtlMerchantManager.getCurrentQuery()))
            dtlLocationManager.getSelectedLocation()
                    .map(DtlLocationCommand::getResult)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(location -> TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                            dtlMerchantManager.getCurrentQuery(),
                            location));
    }

    @Override
    public void onSizeReady(int height) {
        eventBus.post(new DtlMapInfoReadyEvent(height));
    }
}
