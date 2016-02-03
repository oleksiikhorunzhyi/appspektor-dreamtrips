package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionEvent;

import javax.inject.Inject;

public class DtlMapInfoPresenter extends DtlMerchantCommonDetailsPresenter<DtlMapInfoPresenter.View> {

    @Inject
    DtlFilterDelegate dtlFilterDelegate;

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
        view.showDetails(merchant.getId());
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
