package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

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
        filterData.setDistanceType(obtainSetting());
    }

    public DtlFilterData getFilterData() {
        if (filterData == null) init();
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

    private DistanceType obtainSetting() {
        Setting distanceSetting = Queryable.from(db.getSettings()).firstOrDefault(setting ->
                setting.getName().equals(SettingsFactory.DISTANCE_UNITS));
        if (distanceSetting == null) return DistanceType.MILES;
        //
        return DistanceType.provideFromSetting(distanceSetting);
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
