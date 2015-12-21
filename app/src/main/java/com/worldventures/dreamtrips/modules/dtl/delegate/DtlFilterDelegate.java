package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import java.util.ArrayList;
import java.util.List;

public class DtlFilterDelegate {

    private DtlFilterData dtlFilterData;

    private List<FilterListener> listeners = new ArrayList<>();

    public void setDtlFilterData(DtlFilterData dtlFilterData) {
        this.dtlFilterData = dtlFilterData;
    }

    public DtlFilterData getDtlFilterData() {
        return dtlFilterData;
    }

    public DtlFilterData.DistanceType getDistanceType() {
        return dtlFilterData.getDistanceType();
    }

    public void addListener(FilterListener filterListener) {
        listeners.add(filterListener);
    }

    public void removeListener(FilterListener filterListener) {
        listeners.remove(filterListener);
    }

    public void performFiltering() {
        for (FilterListener filterListener : listeners) {
            filterListener.onFilter();
        }
    }

    public interface FilterListener {
        void onFilter();
    }
}
