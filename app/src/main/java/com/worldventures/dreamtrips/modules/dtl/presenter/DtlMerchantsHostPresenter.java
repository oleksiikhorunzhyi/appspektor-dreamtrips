package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

import icepick.State;

public class DtlMerchantsHostPresenter extends Presenter<DtlMerchantsHostPresenter.View> {

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    @State
    boolean initialized;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlMerchantManager.initFilterData();
        //
        if (!initialized) loadMerchants();
        initialized = true;
    }

    private void loadMerchants() {
        dtlMerchantManager.loadMerchants(dtlLocationManager.getCachedSelectedLocation()
                .getCoordinates().asAndroidLocation());
    }

    public void onEvent(final MerchantClickedEvent event) {
        if (view.isTabletLandscape() && view.isFragmentResumed()) {
            eventBus.cancelEventDelivery(event);
            view.showDetails(event.getMerchantId());
        }
    }

    public interface View extends Presenter.View {

        void showDetails(String id);

        boolean isFragmentResumed();
    }
}
