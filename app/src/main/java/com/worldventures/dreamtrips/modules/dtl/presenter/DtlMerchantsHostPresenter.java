package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class DtlMerchantsHostPresenter extends Presenter<DtlMerchantsHostPresenter.View> {

    public void onEventMainThread(final MerchantClickedEvent event) {
        if (view.isTabletLandscape()) view.showDetails(event.getDtlMerchant());
    }

    public interface View extends Presenter.View {

        void showDetails(DtlMerchant merchant);
    }
}
