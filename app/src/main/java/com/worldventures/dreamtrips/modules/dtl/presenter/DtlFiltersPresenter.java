package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

public class DtlFiltersPresenter extends JobPresenter<DtlFiltersPresenter.View> {

    @Inject
    DtlMerchantManager dtlMerchantManager;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        //
        view.attachFilterData(dtlMerchantManager.getFilterData());
        //
        bindJobObservable(dtlMerchantManager.connectMerchantsWithCache())
                .onSuccess(dtlMerchants -> attachAmenities());
    }

    /**
     * Request filter parameters that are currently applied. To use in view to update itself
     */
    public void requestActualFilterData() {
        view.syncUi(dtlMerchantManager.getFilterData());
    }

    private void attachAmenities() {
        view.attachFilterData(dtlMerchantManager.getFilterData());
    }

    /**
     * Apply selected filter parameters
     * @param data new filter parameters
     */
    public void apply() {
        dtlMerchantManager.applyFilter(view.getFilterParameters());
    }

    /**
     * Reset filter parameters to default
     */
    public void resetAll() {
        dtlMerchantManager.reset();
    }

    public interface View extends RxView {

        DtlFilterParameters getFilterParameters();

        /**
         * Fully update UI state with given filter parameters, <br />
         * including (re-)adding amenities to filter
         * @param filterData dataSet to map to UI
         */
        void attachFilterData(DtlFilterData filterData);

        /**
         * Update UI state with given filter parameters
         * @param filterData dataSet to map to UI
         */
        void syncUi(DtlFilterData filterData);
    }
}
