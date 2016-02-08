package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DtlFilterDelegate {

    @Inject
    SnappyRepository db;
    //
    private DtlFilterData filterData;

    public DtlFilterDelegate(Injector injector) {
        injector.inject(this);
    }

    public void init() {
        if (filterData == null) {
            filterData = DtlFilterData.createDefault();
            filterData.setAmenities(db.getAmenities());
            filterData.selectAllAmenities();
        }
        filterData.setDistanceType(db.getMerchantsDistanceType());
    }

    public DtlFilterData getFilterData() {
        return filterData;
    }

    public void applyNewFilter(DtlFilterParameters filterParameters) {
        filterData.from(filterParameters);
        performFiltering();
    }

    public void obtainAmenities() {
        filterData.setAmenities(db.getAmenities());
    }

    public void selectAll() {
        filterData.selectAllAmenities();
    }

    public void reset() {
        filterData.reset();
        performFiltering();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener part
    ///////////////////////////////////////////////////////////////////////////

    private List<FilterListener> filterListeners = new ArrayList<>();

    public void addListener(FilterListener filterListener) {
        filterListeners.add(filterListener);
    }

    public void removeListener(FilterListener filterListener) {
        filterListeners.remove(filterListener);
    }

    public void performFiltering() {
        for (FilterListener filterListener : filterListeners) {
            filterListener.onFilter(filterData);
        }
    }

    public interface FilterListener {
        void onFilter(DtlFilterData filterData);
    }
}
