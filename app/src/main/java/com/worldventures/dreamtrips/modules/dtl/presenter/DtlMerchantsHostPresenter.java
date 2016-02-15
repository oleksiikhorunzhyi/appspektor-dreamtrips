package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;

import javax.inject.Inject;

public class DtlMerchantsHostPresenter extends Presenter<DtlMerchantsHostPresenter.View> {

    @Inject
    DtlFilterDelegate filterDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        filterDelegate.init();
    }

    public void onEventMainThread(final MerchantClickedEvent event) {
        if (view.isTabletLandscape()) view.showDetails(event.getMerchantId());
    }

    public interface View extends Presenter.View {

        void showDetails(String id);
    }
}
