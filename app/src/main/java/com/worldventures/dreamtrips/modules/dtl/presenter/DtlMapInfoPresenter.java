package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;

public class DtlMapInfoPresenter extends DtlMerchantCommonDetailsPresenter<DtlMapInfoPresenter.View> {

    public DtlMapInfoPresenter(String id) {
        super(id);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.hideLayout();
    }

    public void onEvent(DtlShowMapInfoEvent event) {
        view.showLayout();
    }

    public void onMerchantClick() {
        eventBus.post(new ToggleMerchantSelectionEvent(merchant));
        trackIfNeeded();
        view.showDetails(merchant.getId());
    }

    private void trackIfNeeded() {
        if (!TextUtils.isEmpty(dtlMerchantManager.getCurrentQuery()))
            TrackingHelper.trackMerchantOpenedFromSearch(merchant.getMerchantType(),
                    dtlMerchantManager.getCurrentQuery());
    }

    public void onSizeReady(int height) {
        eventBus.post(new DtlMapInfoReadyEvent(height));
    }

    public interface View extends DtlMerchantCommonDetailsPresenter.View {
        void hideLayout();

        void showLayout();

        void showDetails(String id);
    }
}
