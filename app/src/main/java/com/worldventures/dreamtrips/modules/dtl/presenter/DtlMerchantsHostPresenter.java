package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;

public class DtlMerchantsHostPresenter extends JobPresenter<DtlMerchantsHostPresenter.View> {

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
        dtlLocationManager.getSelectedLocation()
                .filter(DtlLocationCommand::isResultDefined)
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> dtlMerchantManager.loadMerchants(location
                        .getCoordinates().asAndroidLocation()));
    }

    public void onEvent(final MerchantClickedEvent event) {
        if (view.isTabletLandscape() && view.isFragmentResumed()) {
            eventBus.cancelEventDelivery(event);
            view.showDetails(event.getMerchantId());
        }
    }

    public interface View extends RxView {

        void showDetails(String id);

        boolean isFragmentResumed();
    }
}
