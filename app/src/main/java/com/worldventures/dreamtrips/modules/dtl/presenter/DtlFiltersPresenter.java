package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import javax.inject.Inject;

public class DtlFiltersPresenter extends Presenter<DtlFiltersPresenter.View> implements
        DtlMerchantRepository.MerchantUpdatedListener {

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlMerchantRepository.attachListener(this);
        //
        if (dtlFilterData == null) {
            dtlFilterData = DtlFilterData.createDefault();
            dtlFilterData.setDistanceType(db.getMerchantsDistanceType());
            dtlFilterData.setAmenities(db.getAmenities());
            dtlFilterData.selectAllAmenities();
        }
        //
        dtlFilterDelegate.setDtlFilterData(dtlFilterData);
        view.attachFilterData(dtlFilterData);
    }

    @Override
    public void dropView() {
        dtlMerchantRepository.detachListener(this);
        super.dropView();
    }

    @Override
    public void onMerchantsUploaded() {
        attachAmenities();
    }

    @Override
    public void onMerchantsFailed(SpiceException spiceException) {
        //nothing to do here
    }

    /**
     * Request filter parameters that are currently applied. To use in view to update itself
     */
    public void requestActualFilterData() {
        view.syncUi(dtlFilterData);
    }

    private void attachAmenities() {
        dtlFilterData.setAmenities(db.getAmenities());
        dtlFilterData.selectAllAmenities();
        view.attachFilterData(dtlFilterData);
    }

    /**
     * Apply selected filter parameters
     * @param data new filter parameters
     */
    public void apply(DtlFilterData data) {
        this.dtlFilterData = dtlFilterData.mutateFrom(data);
        TrackingHelper.dtlMerchantFilter(dtlFilterData);
        dtlFilterDelegate.setDtlFilterData(dtlFilterData);
        dtlFilterDelegate.performFiltering();
    }

    /**
     * Reset filter parameters to default and update view
     */
    public void resetAll() {
        dtlFilterData.reset();
        dtlFilterDelegate.performFiltering();
        view.attachFilterData(dtlFilterData);
    }

    public interface View extends RxView {

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
