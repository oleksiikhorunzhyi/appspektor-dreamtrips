package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import java.util.ArrayList;
import java.util.List;

public class DtlFilterDelegate {

    private DtlFilterData dtlFilterData;
    //
    private List<FilterListener> filterListeners = new ArrayList<>();

    public void setDtlFilterData(DtlFilterData dtlFilterData) {
        this.dtlFilterData = dtlFilterData;
    }

    public DtlFilterData getDtlFilterData() {
        return dtlFilterData;
    }

    public void addListener(FilterListener filterListener) {
        filterListeners.add(filterListener);
    }

    public void removeListener(FilterListener filterListener) {
        filterListeners.remove(filterListener);
    }

    public void performFiltering() {
        for (FilterListener filterListener : filterListeners) {
            filterListener.onFilter();
        }
    }

    public interface FilterListener {
        void onFilter();
    }
}
