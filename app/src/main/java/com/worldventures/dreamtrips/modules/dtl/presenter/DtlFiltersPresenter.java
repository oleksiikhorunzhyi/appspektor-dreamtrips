package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlMerchantDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.FilterAttributesSelectAllEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlPlacesFilterAttribute;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlFiltersPresenter extends Presenter<DtlFiltersPresenter.View> implements
        DtlMerchantDelegate.MerchantUpdatedListener {

    @Inject
    LocationDelegate locationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantDelegate dtlMerchantDelegate;

    @State
    DtlFilterData dtlFilterData;

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchantDelegate.attachListener(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (dtlFilterData == null)
            dtlFilterData = new DtlFilterData();

        dtlFilterDelegate.setDtlFilterData(dtlFilterData);
        attachAmenities();
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

    private void attachAmenities() {
        List<DtlPlacesFilterAttribute> amenities = Queryable.from(db.getAmenities()).map(element ->
                        new DtlPlacesFilterAttribute(element.getName())
        ).toList();

        dtlFilterData.setAmenities(amenities);
        view.attachFilterData(dtlFilterData);
    }

    public void priceChanged(int left, int right) {
        dtlFilterData.setPrice(left, right);
    }

    public void distanceChanged(int right) {
        dtlFilterData.setDistanceType(right);
    }

    public void apply() {
        TrackingHelper.dtlMerchantFilter(dtlFilterData);
        dtlFilterDelegate.performFiltering();
    }

    public void distanceToggle() {
        dtlFilterData.toggleDistance();
        view.attachFilterData(dtlFilterData);
    }

    public void resetAll() {
        dtlFilterData.reset();
        dtlFilterDelegate.performFiltering();
        view.attachFilterData(dtlFilterData);
    }

    private void toggleAmenitiesSelection(boolean selected) {
        dtlFilterData.toggleAmenitiesSelection(selected);
        view.dataSetChanged();
    }

    public interface View extends RxView {

        void attachFilterData(DtlFilterData filterData);

        void dataSetChanged();
    }
}
