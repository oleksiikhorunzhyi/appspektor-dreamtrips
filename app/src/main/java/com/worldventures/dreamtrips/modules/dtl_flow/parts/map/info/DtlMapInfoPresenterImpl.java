package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantService;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationService;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

import javax.inject.Inject;

import flow.Flow;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlFilterMerchantService filterService;
    @Inject
    DtlLocationService locationService;
    //
    protected DtlMerchant merchant;

    public DtlMapInfoPresenterImpl(Context context, Injector injector, String merchantId) {
        super(context);
        injector.inject(this);
        merchantService.getMerchantById(merchantId)
                .compose(ImmediateComposer.instance())
                .subscribe(value -> merchant = value);
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
        filterService.getFilterData()
                .map(DtlFilterData::getSearchQuery)
                .filter(query -> !TextUtils.isEmpty(query))
                .flatMap(query -> locationService.locationPipe().createObservableSuccess(DtlLocationCommand.get())
                        .map(DtlLocationCommand::getResult)
                        .map(location -> new Pair<>(query, location)))
                .subscribe(pair -> {
                    TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                            pair.first,
                            pair.second);
                });
    }

    @Override
    public void onSizeReady(int height) {
        eventBus.post(new DtlMapInfoReadyEvent(height));
    }
}
