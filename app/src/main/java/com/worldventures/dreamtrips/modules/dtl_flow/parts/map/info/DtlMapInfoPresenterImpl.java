package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;

import javax.inject.Inject;

import flow.Flow;

public class DtlMapInfoPresenterImpl extends DtlPresenterImpl<DtlMapInfoScreen, ViewState.EMPTY> implements DtlMapInfoPresenter {

    @Inject
    DtlMerchantInteractor merchantInteractor;
    @Inject
    DtlFilterMerchantInteractor filterInteractor;
    @Inject
    DtlLocationInteractor locationInteractor;
    //
    protected DtlMerchant merchant;

    public DtlMapInfoPresenterImpl(Context context, Injector injector, DtlMerchant merchant) {
        super(context);
        injector.inject(this);
        this.merchant = merchant;
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
        Flow.get(getContext()).set(new DtlMerchantDetailsPath(FlowUtil.currentMaster(getContext()),
                merchant, null));
    }

    private void trackIfNeeded() {
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::getSearchQuery)
                .filter(query -> !TextUtils.isEmpty(query))
                .flatMap(query -> locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
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
