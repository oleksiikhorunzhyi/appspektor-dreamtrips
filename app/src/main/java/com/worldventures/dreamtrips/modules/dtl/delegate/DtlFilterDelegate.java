package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import java.util.ArrayList;
import java.util.List;

public class DtlFilterDelegate {

    private DtlFilterData dtlFilterData;

    private List<FilterListener> filterListeners = new ArrayList<>();
    private List<FilterChangedListener> filterDataChangedListener = new ArrayList<>();

    public void setDtlFilterData(DtlFilterData dtlFilterData) {
        this.dtlFilterData = dtlFilterData;
    }

    public DtlFilterData getDtlFilterData() {
        return dtlFilterData;
    }

    public DtlFilterData.DistanceType getDistanceType() {
        return dtlFilterData.getDistanceType();
    }

    public void setDistanceType(DtlFilterData.DistanceType distanceType) {
        dtlFilterData.setDistanceType(distanceType);
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

    public void addDataChangedListener(FilterChangedListener filterListener) {
        filterDataChangedListener.add(filterListener);
    }

    public void removeDataChangedListener(FilterChangedListener filterListener) {
        filterDataChangedListener.remove(filterListener);
    }

    public void onFilterDataChanged() {
        for (FilterChangedListener filterListener : filterDataChangedListener) {
            filterListener.onFilterDataChanged();
        }
    }

    public interface FilterListener {
        void onFilter();
    }

    public interface FilterChangedListener {
        void onFilterDataChanged();
    }
}
