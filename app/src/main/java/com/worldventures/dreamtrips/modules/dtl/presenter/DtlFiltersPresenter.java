package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.FilterAttributesSelectAllEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsFilterAttribute;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlFiltersPresenter extends Presenter<DtlFiltersPresenter.View> implements
        DtlMerchantRepository.MerchantUpdatedListener, DtlFilterDelegate.FilterChangedListener {

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;

    @State
    DtlFilterData dtlFilterData;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlMerchantRepository.attachListener(this);
        //
        if (dtlFilterData == null) {
            dtlFilterData = DtlFilterData.createDefault();
            dtlFilterData.setDistanceType(db.getDistanceType());
        }

        dtlFilterDelegate.addDataChangedListener(this);
        dtlFilterDelegate.setDtlFilterData(dtlFilterData);
        attachAmenities();
    }

    @Override
    public void dropView() {
        dtlFilterDelegate.removeDataChangedListener(this);
        dtlMerchantRepository.detachListener(this);
        super.dropView();
    }

    @Override
    public void onFilterDataChanged() {
        view.attachFilterData(dtlFilterData);
    }

    public void onEvent(FilterAttributesSelectAllEvent event) {
        toggleAmenitiesSelection(event.isChecked());
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
        view.attachFilterData(dtlFilterData);
    }

    private void attachAmenities() {
        List<DtlMerchantsFilterAttribute> amenities = Queryable.from(db.getAmenities())
                .map(element -> new DtlMerchantsFilterAttribute(element.getName())).toList();

        dtlFilterData.setAmenities(amenities);
        view.attachFilterData(dtlFilterData);
    }

    /**
     * Apply selected filter parameters
     * @param data new filter parameters
     */
    public void apply(DtlFilterData data) {
        TrackingHelper.dtlMerchantFilter(data);
        db.saveDistanceToggle(data.getDistanceType());
        this.dtlFilterData = data;
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

    /**
     * Batch toggle all amenities
     * @param selected select all
     */
    private void toggleAmenitiesSelection(boolean selected) {
        dtlFilterData.toggleAmenitiesSelection(selected);
        view.dataSetChanged();
    }

    public interface View extends RxView {

        /**
         * Update UI state with given filter parameters
         * @param filterData dataSet to map to UI
         */
        void attachFilterData(DtlFilterData filterData);

        void dataSetChanged();
    }
}
