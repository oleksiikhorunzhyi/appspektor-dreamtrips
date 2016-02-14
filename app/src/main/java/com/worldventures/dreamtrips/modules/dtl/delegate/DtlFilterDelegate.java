package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class DtlFilterDelegate {

    @Inject
    SnappyRepository db;
    //
    private DtlFilterData filterData;
    //
    private final BehaviorSubject<DtlFilterData> filterStream;

    public DtlFilterDelegate(Injector injector) {
        injector.inject(this);
        this.filterStream = BehaviorSubject.create();
    }

    public void init() {
        if (filterData == null) {
            filterData = DtlFilterData.createDefault();
            filterData.setAmenities(db.getAmenities());
            filterData.selectAllAmenities();
        }
        filterData.setDistanceType(db.getMerchantsDistanceType());
        filterStream.onNext(filterData);
    }

    public Observable<DtlFilterData> getFilterStream(){
        return filterStream;
    }

    public void applySearch(String query) {
        filterData.setSearchQuery(query);
        filterStream.onNext(filterData);
    }

    public void applyFilter(DtlFilterParameters filterParameters) {
        filterData.from(filterParameters);
        filterStream.onNext(filterData);
    }

    public DtlFilterData getFilterData() {
        return filterData;
    }

    public void obtainAmenities() {
        filterData.setAmenities(db.getAmenities());
        filterData.selectAllAmenities();
    }

    public void reset() {
        filterData.reset();
        filterStream.onNext(filterData);
    }
}
